package com.gaboratorium.stash.resources.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.resources.users.dao.User;
import com.gaboratorium.stash.resources.users.dao.UserDao;
import com.gaboratorium.stash.resources.users.requests.RegisterUserRequestBody;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserResource {

    // Constructor

    private final ObjectMapper mapper;
    private final UserDao userDao;

    // Endpoints

    @Path("/{id}")
    @GET
    public Response getUser(
        @PathParam("id") final String userId
    ) {
        final User user = userDao.findById(userId);
        final boolean isUserFound = user != null;

        return isUserFound ?
            StashResponse.ok(user) :
            StashResponse.notFound();
    }

    // TODO: How do we know the appId?

    @POST
    public Response registerUser(
        @Valid @NotNull final RegisterUserRequestBody body
    ) {

        final boolean isUserIdFree = userDao.findById(body.userId) == null;
        final boolean isUserEmailFree = userDao.findByUserEmail(body.userEmail) == null;

        if (!isUserIdFree) {
            return StashResponse.conflict("This user ID is already taken.");
        }
        if (!isUserEmailFree) {
            return StashResponse.conflict("This user e-mail address is already taken.");
        }

        final User user =userDao.insert(
            body.userId,
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
}
