package com.gaboratorium.stash.resources.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.resources.apps.requests.CreateAppRequestBody;
import com.gaboratorium.stash.resources.apps.requests.HeaderParams;
import com.gaboratorium.stash.resources.users.dao.User;
import com.gaboratorium.stash.resources.users.dao.UserDao;
import com.gaboratorium.stash.resources.users.requests.RegisterUserRequestBody;
import com.gaboratorium.stash.resources.users.requests.UpdateUserRequestBody;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserResource {

    // Constructor

    private final ObjectMapper mapper;
    private final UserDao userDao;

    // Endpoints

    @POST
    @AppAuthenticationRequired
    public Response registerUser(
        @HeaderParam(HeaderParams.APP_ID) final String appId,
        @Valid @NotNull final RegisterUserRequestBody body
    ) {

        final boolean isUserIdFree = userDao.findById(body.userId, appId) == null;
        final boolean isUserEmailFree = userDao.findByUserEmail(body.userEmail, appId) == null;

        if (!isUserIdFree) {
            return StashResponse.conflict("This user ID is already taken.");
        }
        if (!isUserEmailFree) {
            return StashResponse.conflict("This user e-mail address is already taken.");
        }

        final User user = userDao.insert(
            body.userId,
            body.appId,
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

        return StashResponse.ok(user);
    }

    @GET
    @Path("/{user_id}")
    @AppAuthenticationRequired
    public Response getUser(
        @HeaderParam(HeaderParams.APP_ID) final String appId,
        @PathParam("user_id") final String userId
    ) {
        final User user = userDao.findById(userId, appId);
        final boolean isUserFound = user != null;

        return isUserFound ?
            StashResponse.ok(user) :
            StashResponse.notFound();
    }

    @PUT
    @Path("/{user_id}")
    @AppAuthenticationRequired
    public Response updateUser(
        @HeaderParam(HeaderParams.APP_ID) final String appId,
        @PathParam("user_id") final String userId,
        @Valid @NotNull final UpdateUserRequestBody body
    ) {
        final User currentUser = userDao.findById(userId, appId);

        if (currentUser == null) {
            return StashResponse.notFound();
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


    private User createUpdatedUser(UpdateUserRequestBody requestBody, User currentUser) {

        // TODO: Merge UpdateUserRequestBody with currentUser: there must be an easier way

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
