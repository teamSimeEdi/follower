package si.RSOteam8;


import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;


import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("followers")
@Log
public class FollowerResource {



    @Inject
    private ConfigProperties cfg;

    @GET
    @Path("{userid}")
    public Response getAllFollowers(@PathParam("userid") String userid) {
        List<Follower> followers = new LinkedList<Follower>();

        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM followers.followers WHERE \"userid\" = "+"'"+userid+"'");
        ) {
            while (rs.next()) {
                Follower follower = new Follower();
                follower.setId(rs.getString(1));
                follower.setFollower(rs.getString(3));
                followers.add(follower);
            }
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(followers).build();
    }
    /*@Counted(name = "getAllFollowers-count")
    @GET
    public Response getAllFollowers() {
        Logger.getLogger(FollowerHealthCheck.class.getSimpleName()).info("just testing");
        List<Follower> followers = new LinkedList<Follower>();
        Follower follower = new Follower();
        follower.setId("1");
        follower.setFollowername(cfg.getTest());
        followers.add(follower);
        follower = new Follower();
        follower.setId("2");
        follower.setFollowername("peterklepec");
        followers.add(follower);
        return Response.ok(followers).build();
    }*/

    @GET
    @Path("{userId}/{followerId}")
    public Response getFollower(@PathParam("followerId") String followerid) {

        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM followers.followers WHERE \"id\" = "+"'"+followerid+"'");
        ) {
            if (rs.next()){
                Follower follower = new Follower();
                follower.setId(rs.getString(1));
                follower.setFollower(rs.getString(2));
                return Response.ok(follower).build();

            }
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

   /* @POST
    public Response addNewFollower(Follower follower) {
        //Database.addCustomer(customer);
        return Response.noContent().build();
    }*/
    @POST
    @Path("{userId}")
    public Response addNewFollower(@PathParam("userId") String username,
                                  Follower follower
                                  ) {
        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate("INSERT INTO followers.followers (userid, follower) VALUES ('"
                    + username + "', '"+ follower.getFollower() + "')",
            Statement.RETURN_GENERATED_KEYS);
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }
    @DELETE
    @Path("{userid}/{followerId}")
    public Response deleteFollower(@PathParam("userid") String userid,
                                    @PathParam("followerId") String followerid) {
        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate("DELETE FROM followers.followers WHERE \"follower\" = " + "'"+followerid+"'"+" AND \"userid\" = "+"'"+userid+"'");
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

            return Response.noContent().build();
    }
    @DELETE
    @Path("{userid}")
    public Response deleteUser(@PathParam("userid") String userid) {
        try (
                Connection conn = DriverManager.getConnection(cfg.getDburl(), cfg.getDbuser(), cfg.getDbpass());
                Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate("DELETE FROM followers.followers WHERE \"userid\" = "+"'"+userid+"'");
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }
    /*@DELETE
    @Path("{followerId}")
    public Response deleteFollower(@PathParam("followerId") String followerId) {
        //Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }*/
}
