package com.gaboratorium.stash.resources.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.requestAuthorizator.RequestGuard;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationHeaders;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationRequired;
import com.gaboratorium.stash.resources.files.dao.File;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Path("/apps")
@RequiredArgsConstructor
public class FileResource {

    private final ObjectMapper objectMapper;
    private final FileDao fileDao;
    private final StashTokenStore stashTokenStore;
    private final RequestGuard appRequestGuard;
    private final RequestGuard userRequestGuard;

    // private final String uploadPath = "C:/Users/gaboratorium/Desktop/";
    private final String uploadPath = "";

    @POST
    @Path("/{appId}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AppAuthenticationRequired
    public Response uploadFile(
        @NotNull @Valid @FormDataParam("file") InputStream inputStream,
        @NotNull @Valid @FormDataParam("file") FormDataBodyPart fileDetails,
        @NotNull @Valid @PathParam("appId") String appId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) Optional<String> appIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) Optional<String> userIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_TOKEN) Optional<String> userTokenHeader,
        @Valid @FormDataParam("isPublic") Boolean isPublic,
        @Valid @FormDataParam("ownerId") String ownerId
    )  {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        }

        final boolean isOwnerIdProvided = ownerId != null;
        final boolean isFilePublic = isPublic == null || isPublic;

        if (userRequestGuard.isAuthenticationRequired() && ownerId != null) {

            if (!(userTokenHeader.isPresent() && userIdHeader.isPresent())) {
                return StashResponse.forbidden("User authentication token or ID header is not present");
            } else if (!userRequestGuard.isRequestAuthorized(userIdHeader, ownerId)) {
                return StashResponse.forbidden("Requested user ID and requester user ID don't match.");
            } else if (!stashTokenStore.isValid(userTokenHeader.get(), userIdHeader.get())) {
                return StashResponse.forbidden("User authentication token is not valid.");
            }
        }

        final String ownerNameInPath = isOwnerIdProvided ? ownerId  : "common";
        final String fileUploadPath = uploadPath + ownerNameInPath + "/";

        final String fileName = fileDetails.getContentDisposition().getFileName();
        final File checkedFile = fileDao.findByNameAndOwner(fileName, appId, ownerId);
        final boolean isFileTaken = checkedFile != null;
        if (isFileTaken) {
            return StashResponse
                .conflict("File already exists. Try to call Update File instead, or upload another file.");
        }

        try {
            final java.io.File targetFile  = new java.io.File(fileUploadPath + fileName);
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
            final String fileId = UUID.randomUUID().toString();
            final File file = fileDao.insert(
                fileId,
                appId,
                fileUploadPath,
                fileName,
                ownerId,
                isFilePublic
            );

            return StashResponse.created(file);
        } catch (IOException error) {
            return StashResponse.error(error.getMessage());
        }

    }

    @GET
    @Path("/{appId}/files/{fileName}")
    @AppAuthenticationRequired
    public Response getFile(
        @NotNull @PathParam("appId") String appId,
        @NotNull @PathParam("fileName") String fileName,
        @QueryParam("ownerId") Optional<String> ownerId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) Optional<String> appIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) Optional<String> userIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_TOKEN) Optional<String> userTokenHeader
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        }

        final File file = getFileByNameAndOwnerId(fileName, appId, ownerId.orElse(null));

        if (file == null) {
            return Response.status(404).entity("File not found.").build();
        }

        final boolean isFileAccessible =  file.isPublic() || isOwnerAuthenticated(
            file,
            userIdHeader,
            userTokenHeader
        );

        if (isFileAccessible) {

            final java.io.File targetFile = new java.io.File(file.getFilePath() + file.getFileName());
            final String mimeType = URLConnection.guessContentTypeFromName(targetFile.getName());
            final String contentDisposition = String.format("inline; filename=%s", targetFile.getName());

            return Response.ok(targetFile, mimeType)
                .header("Content-Disposition", contentDisposition)
                .build();
        } else {

            return Response.status(403).entity("Access denied.").build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{appId}/files/{fileName}")
    @AppAuthenticationRequired
    @UserAuthenticationRequired
    public Response deleteFile(
        @NotNull @PathParam("appId") String appId,
        @NotNull @PathParam("fileName") String fileName,
        @QueryParam("ownerId") Optional<String> ownerId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) Optional<String> appIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) Optional<String> userIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_TOKEN) Optional<String> userTokenHeader
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden("App authorization failed.");
        }

        final String fileOwnerId;
        fileOwnerId = ownerId.orElse(null);

        final File file = fileDao.findByNameAndOwner(fileName, appId, fileOwnerId);

        final boolean isFileFound = file != null;

        if (!isFileFound) {
            return StashResponse.notFound("File not found.");
        }

        if (!userRequestGuard.isRequestAuthorized(userIdHeader, ownerId.orElse(null)) ) {
            return StashResponse.forbidden("User authorization failed.");
        }

        try {
            final java.nio.file.Path path = Paths.get(file.getFilePath() + file.getFileName());
            Files.delete(path);
            fileDao.delete(file.getFileId(), appId);
            return StashResponse.noContent();

        } catch (IOException e) {
            return StashResponse.error(e.getMessage());
        }
    }

    // Helper methods

    private File getFileByNameAndOwnerId(
        String fileName,
        String appId,
        String ownerId
    ) {
        return ownerId == null ?
            fileDao.findOwnerlessFileByName(fileName, appId) :
            fileDao.findByNameAndOwner(fileName, appId, ownerId);
    }

    private boolean isOwnerAuthenticated(
        File file,
        Optional<String> userId,
        Optional<String> userToken
    ) {
        return userId.isPresent() &&
            userToken.isPresent() &&
            stashTokenStore.isValid(userToken.get(), userId.get()) &&
            file.getFileOwnerId().equals(userId.get());
    }

}
