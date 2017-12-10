package com.gaboratorium.stash.resources.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.requestAuthorizator.RequestGuard;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationHeaders;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationRequired;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.users.dao.User;
import com.gaboratorium.stash.resources.users.dao.UserDao;
import com.gaboratorium.stash.resources.users.requests.AuthenticateUserRequestBody;
import com.gaboratorium.stash.resources.users.requests.RegisterUserRequestBody;
import com.gaboratorium.stash.resources.users.requests.UpdateUserRequestBody;
import io.dropwizard.jersey.PATCH;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.Optional;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/apps")
@RequiredArgsConstructor
public class UserResource {

    // Constructor

    private final ObjectMapper mapper;
    private final UserDao userDao;
    private final AppDao appDao;
    private final StashTokenStore stashTokenStore;
    private final RequestGuard appRequestGuard;
    private final RequestGuard userRequestGuard;

    // Endpoints

    @POST
    @AppAuthenticationRequired
    @Path("/{appId}/users")
    public Response registerUser(
        @NotNull @Valid final RegisterUserRequestBody body,
        @NotNull @PathParam("appId") final String appId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        }

        final boolean isUserIdFree = userDao.findById(body.userId, appId) == null;
        final boolean isUserEmailFree = userDao.findByUserEmail(body.userEmail, appId) == null;
        final App app = appDao.findById(appId);

        final boolean isAppFound = app != null;

        if (!isAppFound) {
            return StashResponse.notFound("App not found.");
        }

        if (!isUserIdFree) {
            return StashResponse.conflict("This user ID is already taken.");
        }
        if (!isUserEmailFree) {
            return StashResponse.conflict("This user e-mail address is already taken.");
        }

        final User user = userDao.insert(
            body.userId,
            appId,
            body.userEmail,
            body.userPasswordHash,
            body.userEmailSecondary,
            body.userFirstName,
            body.userLastName,
            body.userGender,
            body.userRole,
            body.userAddress,
            body.userCity,
            body.userZip,
            body.userCountry,
            body.userBirthday
        );

        return StashResponse.created(user);
    }

    @GET
    @Path("/{appId}/users/{user_id}")
    @AppAuthenticationRequired
    public Response getUser(
        @NotNull @PathParam("appId") final String appId,
        @NotNull @PathParam("user_id") final String userId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        }

        final User user = userDao.findById(userId, appId);
        final boolean isUserFound = user != null;

        return isUserFound ?
            StashResponse.ok(user) :
            StashResponse.notFound();
    }

    @PATCH
    @Path("/{appId}/users/{userId}")
    @AppAuthenticationRequired
    @UserAuthenticationRequired
    public Response updateUser(
        @NotNull @PathParam("appId") final String appId,
        @NotNull @PathParam("userId") final String userId,
        @NotNull @Valid final UpdateUserRequestBody body,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) final Optional<String> userIdHeader,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId) ||
            !userRequestGuard.isRequestAuthorized(userIdHeader, userId)) {
            return StashResponse.forbidden();
        }

        final User currentUser = userDao.findById(userId, appId);
        final boolean isUserNotFound = currentUser == null;

        if (isUserNotFound) {
            return StashResponse.notFound();
        }

        if (!userRequestGuard.isRequestAuthorized(userIdHeader, userId)) {
            return StashResponse.forbidden("User authorization failed.");
        }

        final User updatedUser = createUpdatedUser(body, currentUser);

        final User updatedUserFromDb = userDao.update(
            currentUser.getUserId(),
            updatedUser.getUserId(),
            currentUser.getAppId(),
            updatedUser.getUserEmail(),
            updatedUser.getUserPasswordHash(),
            updatedUser.getUserEmailSecondary(),
            updatedUser.getUserFirstName(),
            updatedUser.getUserLastName(),
            updatedUser.getUserGender(),
            updatedUser.getUserRole(),
            updatedUser.getUserAddress(),
            updatedUser.getUserCity(),
            updatedUser.getUserZip(),
            updatedUser.getUserCountry(),
            updatedUser.getUserBirthday()
        );

        return StashResponse.ok(updatedUserFromDb);
    }

    @DELETE
    @Path("/{appId}/users/{userId}")
    @AppAuthenticationRequired
    @UserAuthenticationRequired
    public Response deleteUser(
        @NotNull @PathParam("appId") final String appId,
        @NotNull @PathParam("userId") final String userId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) final Optional<String> userIdHeader
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId) ||
            !userRequestGuard.isRequestAuthorized(userIdHeader, userId)) {
            return StashResponse.forbidden("App authorization failed.");
        }

        final User user = userDao.findById(userId, appId);
        final boolean isUserNotFound = user == null;

        if (isUserNotFound) {
            return StashResponse.notFound();
        }

        if (!userRequestGuard.isRequestAuthorized(userIdHeader, userId)) {
            return StashResponse.forbidden("User authorization failed.");
        }

        userDao.delete(user.getUserId(), user.getAppId());
        return StashResponse.noContent();
    }

    @POST
    @Path("/{appId}/users/{userId}/authenticate")
    @AppAuthenticationRequired
    public Response authenticateUser(
        @NotNull @PathParam("appId") final String appId,
        @NotNull @PathParam("userId") final String userId,
        @NotNull @Valid final AuthenticateUserRequestBody body,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden("App is not authorized to perform this action");
        }

        final User user = userDao.findByUserCredentials(userId, body.getUserPasswordHash(), appId);
        final boolean isUserNotFound = user == null;

        if (isUserNotFound) {
            return StashResponse.forbidden("User does not exist or wrong credentials");
        }

        final String token = stashTokenStore.create(userId, stashTokenStore.getUserAuthTokenExpiryTime());
        return StashResponse.ok(token);
    }


    private User createUpdatedUser(UpdateUserRequestBody requestBody, User currentUser) {

        final String userId = requestBody.getUserId() == null ?
            currentUser.getUserId() :
            requestBody.getUserId();

        final String userEmail = requestBody.getUserEmail() == null ?
            currentUser.getUserEmail() :
            requestBody.getUserEmail();

        final String userPasswordHash = requestBody.getUserPasswordHash() == null ?
            currentUser.getUserPasswordHash() :
            requestBody.getUserPasswordHash();

        final String userEmailSecondary = requestBody.getUserEmailSecondary() == null ?
            currentUser.getUserEmailSecondary() :
            requestBody.getUserEmailSecondary();

        final String userFirstName = requestBody.getUserFirstName() == null ?
            currentUser.getUserFirstName() :
            requestBody.getUserFirstName();

        final String userLastName = requestBody.getUserLastName() == null ?
            currentUser.getUserLastName() :
            requestBody.getUserLastName();

        final String userGender = requestBody.getUserGender() == null ?
            currentUser.getUserGender() :
            requestBody.getUserGender();

        final String userRole = requestBody.getUserRole() == null ?
            currentUser.getUserRole() :
            requestBody.getUserRole();

        final String userAddress = requestBody.getUserAddress() == null ?
            currentUser.getUserAddress() :
            requestBody.getUserAddress();

        final String userCity = requestBody.getUserCity() == null ?
            currentUser.getUserCity() :
            requestBody.getUserCity();

        final String userZip = requestBody.getUserZip() == null ?
            currentUser.getUserZip() :
            requestBody.getUserZip();

        final String userCountry = requestBody.getUserCountry() == null ?
            currentUser.getUserCountry() :
            requestBody.getUserCountry();

        final Timestamp userBirthday = requestBody.getUserBirthday() == null ?
            currentUser.getUserBirthday() :
            requestBody.getUserBirthday();

        return new User(
            userId,
            currentUser.getAppId(),
            userEmail,
            userPasswordHash,
            userEmailSecondary,
            userFirstName,
            userLastName,
            userGender,
            userRole,
            userAddress,
            userCity,
            userZip,
            userCountry,
            userBirthday,
            currentUser.getUserRegisteredAt()
        );
    }
}
