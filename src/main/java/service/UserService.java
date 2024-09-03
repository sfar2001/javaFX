package service;

import Entities.User;
import Interfaces.IService;
import Utils.ConnectionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService implements IService<User> {

    public User userConnect;
    private static UserService instance;
    private User authenticatedUser; // Store authenticated user here
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    @Override
    public void create(User entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user (nom, roles, password, email, prenom, role, is_verified, adress, ville, zipcode, reset_token, reset_token_expires_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            preparedStatement.setString(1, entity.getNom());
            preparedStatement.setString(2, entity.getRoles());
            preparedStatement.setString(3, entity.getPassword());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getPrenom());
            preparedStatement.setString(6, entity.getRole());
            preparedStatement.setBoolean(7, entity.isIs_verified());
            preparedStatement.setString(8, entity.getAdress());
            preparedStatement.setString(9, entity.getVille());
            preparedStatement.setInt(10, entity.getZipcode());
            preparedStatement.setString(11, entity.getReset_token());
            preparedStatement.setTimestamp(12, entity.getReset_token_expired_at());

            preparedStatement.executeUpdate();
            System.out.println("User created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public void update(User entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE user SET nom = ?, roles = ?, password = ?, email = ?, prenom = ?, role = ?, is_verified = ?, adress = ?, ville = ?, zipcode = ?, reset_token = ?, reset_token_expires_at = ? WHERE id = ?")) {

            preparedStatement.setString(1, entity.getNom());
            preparedStatement.setString(2, entity.getRoles());
            preparedStatement.setString(3, entity.getPassword());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getPrenom());
            preparedStatement.setString(6, entity.getRole());
            preparedStatement.setBoolean(7, entity.isIs_verified());
            preparedStatement.setString(8, entity.getAdress());
            preparedStatement.setString(9, entity.getVille());
            preparedStatement.setInt(10, entity.getZipcode());
            preparedStatement.setString(11, entity.getReset_token());
            preparedStatement.setTimestamp(12, entity.getReset_token_expired_at());
            preparedStatement.setInt(13, entity.getId());

            preparedStatement.executeUpdate();
            System.out.println("User updated successfully");
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(User entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user WHERE id = ?")) {

            preparedStatement.setInt(1, entity.getId());

            preparedStatement.executeUpdate();
            System.out.println("User deleted successfully");
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        List<User> userList = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM user")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String roles = resultSet.getString("roles");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String prenom = resultSet.getString("prenom");
                String role = resultSet.getString("role");
                boolean is_verified = resultSet.getBoolean("is_verified");
                String adress = resultSet.getString("adress");
                String ville = resultSet.getString("ville");
                int zipcode = resultSet.getInt("zipcode");
                String reset_token = resultSet.getString("reset_token");
                Timestamp reset_token_expired_at = resultSet.getTimestamp("reset_token_expires_at");

                User user = new User(id, nom, roles, password, email, prenom, role, is_verified, adress, ville, zipcode, reset_token, reset_token_expired_at);
                userList.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return userList;
    }

    public boolean SignUpUser(User user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean success = false;
        String query = "INSERT INTO user (nom, roles, password, email, prenom, role, is_verified, adress, ville, zipcode, reset_token, reset_token_expires_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = ConnectionManager.getConnection();
            if (conn == null) {
                // Handle the case where the connection is null
                throw new SQLException("Failed to establish database connection.");
            }
            preparedStatement = conn.prepareStatement("SELECT email FROM user WHERE email = ?");
            preparedStatement.setString(1, user.getEmail());
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);
                String hashedPassword = passwordEncoder.encode(user.getPassword());

                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, user.getNom());
                preparedStatement.setString(2, user.getRoles());
                preparedStatement.setString(3, hashedPassword);
                preparedStatement.setString(4, user.getEmail());
                preparedStatement.setString(5, user.getPrenom());
                preparedStatement.setString(6, "test");
                preparedStatement.setBoolean(7, user.isIs_verified());
                preparedStatement.setString(8, user.getAdress());
                preparedStatement.setString(9, user.getVille());
                preparedStatement.setInt(10, user.getZipcode());
                preparedStatement.setString(11, user.getReset_token());
                preparedStatement.setTimestamp(12, user.getReset_token_expired_at());

                preparedStatement.executeUpdate();
                success = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public User authenticate(String email, String password) {
        User user = null;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionManager.getConnection();
            String query = "SELECT * FROM user WHERE email = ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String hashedPasswordFromDB = resultSet.getString("password");
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);
                if (passwordEncoder.matches(password, hashedPasswordFromDB)) {
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("roles"),
                            resultSet.getString("password"),
                            resultSet.getString("email"),
                            resultSet.getString("prenom"),
                            resultSet.getString("role"),
                            resultSet.getBoolean("is_verified"),
                            resultSet.getString("adress"),
                            resultSet.getString("ville"),
                            resultSet.getInt("zipcode"),
                            resultSet.getString("reset_token"),
                            resultSet.getTimestamp("reset_token_expires_at")
                    );
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        authenticatedUser = user;
        return user;
    }

    public boolean ForgetPassUser(String email, String newPassword) throws Exception {
        User user = getUserByEmail(email); // Ensure user is retrieved and has a valid ID
        if (user == null) {
            System.out.println("User not found in the database!!");
            return false;
        }

        System.out.println("User found with ID: " + user.getId()); // Debug statement
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);
        String hashedPassword = passwordEncoder.encode(newPassword);
        String query = "UPDATE user SET password = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, user.getId()); // Use the valid user ID
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected); // Debug statement
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByEmail(String email) {
        User user = null;
        String query = "SELECT * FROM user WHERE email = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("roles"),
                            resultSet.getString("password"),
                            resultSet.getString("email"),
                            resultSet.getString("prenom"),
                            resultSet.getString("role"),
                            resultSet.getBoolean("is_verified"),
                            resultSet.getString("adress"),
                            resultSet.getString("ville"),
                            resultSet.getInt("zipcode"),
                            resultSet.getString("reset_token"),
                            resultSet.getTimestamp("reset_token_expires_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }

        return user;
    }

    // Method to get user ID by email
    public int getUserIdByEmail(String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            return user.getId(); // Return the ID if the user is found
        } else {
            System.err.println("User not found for email: " + email);
            return -1; // Return an invalid ID or handle as needed
        }
    }



    public boolean  isEmailExist(String email) {
        boolean emailExists = false;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT email FROM user WHERE email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                emailExists = resultSet.next(); // If there's a result, email exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return emailExists;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

}
