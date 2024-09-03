package Controllers;

import Entities.User;
import service.ResetPasswordService;
import service.UserService;
import Utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardAdminController implements Initializable {

    @FXML
    private TableColumn<User, Button> SupprimerCol;


    @FXML
    private TableColumn<User, String> adressCol;



    @FXML
    private ScrollPane boxh;

    @FXML
    private Button btnChart;

    @FXML
    private Button btnModifProfil;

    @FXML
    private Button btnResetPass;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnsignUp;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TableColumn<User, String> emailCol;

    @FXML
    private TableColumn<User, String> familynameCol;


    @FXML
    private TableColumn<User, Boolean> isVerifiedCol;

    @FXML
    private Label lbAdress;

    @FXML
    private Label lbConfirmEmailAdmin;

    @FXML
    private Label lbConfirmPasswordAdmin;


    @FXML
    private Label lbEmailAdmin;


    @FXML
    private Label lbNomAdmin;


    @FXML
    private Label lbPasswordAdmin;


    @FXML
    private Label lbPrenomAdmin;


    @FXML
    private Pane pnAddAdmin;

    @FXML
    private Pane pnUsers;

    @FXML
    private Pane pnprofile;

    @FXML
    private TableView<User> tableViewUsers;

    @FXML
    private TextField tfAdress;

    @FXML
    private TextField tfAdresse;

    @FXML
    private TextField tfConfirmEmailAdmin;

    @FXML
    private PasswordField tfConfirmPassword;

    @FXML
    private PasswordField tfConfirmPasswordAdmin;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfEmailAdmin;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfNomAdmin;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private PasswordField tfPasswordAdmin;

    @FXML
    private TextField tfPrenom;

    @FXML
    private TextField tfPrenomAmin;

    @FXML
    private TextField tfSearch;

    @FXML
    private TableColumn<User, String> usernameCol;

    @FXML
    private Label lbUserNameGlobal;

    private Stage stage;

    private Image image_url;

    private UserService userService;

    ObservableList<User> userList = FXCollections.observableArrayList();

    Button blockButton = null;

    private boolean bConfirmEmail=false,bEqualPassword = false, bNom, bPrenom, bEmail = false, bAdd = false, bPass = false, bRole;


    @FXML
    void add(ActionEvent event) {

    }

    @FXML
    void afficher(ActionEvent event) {

    }





    @FXML
    void fnSignup(ActionEvent event) throws IOException {
        UserService ps = new UserService();
        clearErrorLabels();
        bNom = !tfNomAdmin.getText().isEmpty();
        bPrenom = !tfPrenomAmin.getText().isEmpty();
        bEmail = isValidEmail(tfEmailAdmin.getText());
        bConfirmEmail = tfConfirmEmailAdmin.getText().equals(tfEmailAdmin.getText());
        String passwordError = isValidPassword(tfPasswordAdmin.getText(), tfNomAdmin.getText(), tfPrenomAmin.getText());
        bPass = passwordError == null;
        bEqualPassword = bPass && tfPasswordAdmin.getText().equals(tfConfirmPasswordAdmin.getText());

        if (bNom && bPrenom && bEmail && bConfirmEmail && bPass && bEqualPassword && bAdd) {
            String address = tfAdress.getText();
            Pattern pattern = Pattern.compile("^(\\d{4}),\\s*([^,]+),\\s*(.+)$");
            Matcher matcher = pattern.matcher(address);
            if (matcher.matches()) {
                int zipCode = Integer.parseInt(matcher.group(1));
                String ville = matcher.group(2);
                String fullAddress = matcher.group(3);

                if (ps.SignUpUser(new User(tfNomAdmin.getText(), tfPasswordAdmin.getText(),tfEmailAdmin.getText(), tfPrenomAmin.getText(),  fullAddress, ville, zipCode, "[\"ROLE_ADMIN\"]"))) {
                    pnUsers.toFront();
                    fnReloadData();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Account created successfully!");
                    alert.setHeaderText(null);
                    alert.setTitle("Success");
                    alert.show();
                } else {
                    showErrorAlertAdmin("Email already exists in the database!!");
                }
            } else {
                showErrorAlertAdmin("Invalid address format. Please use the format: zipCode, Ville, Address");
            }
        } else {
            showErrorAlertAdmin("All information should be valid!");
        }
    }

    private void clearErrorLabels() {
        lbNomAdmin.setText("");
        lbEmailAdmin.setText("");
        lbAdress.setText("");
        lbPasswordAdmin.setText("");
        lbConfirmPasswordAdmin.setText("");
    }

    private void showErrorAlertAdmin(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle("Invalid Information");
        alert.setHeaderText(null);
        alert.show();

        if (!bNom) {
            lbNomAdmin.setText("Please enter your family name");
        }
        if (!bPrenom) {
            lbPrenomAdmin.setText("Please enter your first name");
        }
        if (!bEmail) {
            lbEmailAdmin.setText("Please enter a valid email");
        }
        if (!bPass) {
            lbPasswordAdmin.setText("Please enter a valid password");
        }
        if (!tfConfirmPassword.getText().isEmpty() && !bEqualPassword) {
            lbConfirmPasswordAdmin.setText("Passwords do not match");
        }
        if (!bAdd) {
            lbAdress.setText("Please enter a valid address");
        }

    }

    private void validateEmail(KeyEvent event) {
        String email = tfEmailAdmin.getText();
        bEmail = isValidEmail(email);
        lbEmailAdmin.setText(bEmail ? "" : "Please enter a valid email");
    }

    private void validateConfirmEmail(KeyEvent event) {
        String confirmEmail = tfConfirmEmailAdmin.getText();
        bConfirmEmail = confirmEmail.equals(tfEmailAdmin.getText());
        lbConfirmEmailAdmin.setText(bConfirmEmail ? "" : "Emails do not match");
    }

    private void validatePassword(KeyEvent event) {
        String password = tfPasswordAdmin.getText();
        String passwordError = isValidPassword(password, tfNomAdmin.getText(), tfPrenomAmin.getText());
        bPass = passwordError == null;
        lbPasswordAdmin.setText(bPass ? "" : passwordError);
    }

    private void validateAddress(KeyEvent event) {
        String address = tfAdress.getText();
        Pattern pattern = Pattern.compile("^(\\d{4}),\\s*([^,]+),\\s*(.+)$");
        Matcher matcher = pattern.matcher(address);
        bAdd = matcher.matches();
        lbAdress.setText(bAdd ? "" : "Invalid address format. Please use the format: zipCode, Ville, Address");
    }

    private void validateNom(KeyEvent event) {
        bNom = !tfNomAdmin.getText().isEmpty();
        lbNomAdmin.setText(bNom ? "" : "Please enter your family name");
    }

    private void validatePrenom(KeyEvent event) {
        bPrenom = !tfPrenomAmin.getText().isEmpty();
        lbPrenomAdmin.setText(bPrenom ? "" : "Please enter your first name");
    }


    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailPattern);
    }

    private String isValidPassword(String password, String Nom, String Prenom) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit";
        }
        if (Nom != null && !Nom.isEmpty() && password.toLowerCase().contains(Nom.toLowerCase())) {
            return "Password cannot contain your family name";
        }
        if (Prenom != null && !Prenom.isEmpty() && password.toLowerCase().contains(Prenom.toLowerCase())) {
            return "Password cannot contain your name";
        }
        return null;
    }






    @FXML
    void fnLogOut(ActionEvent event) {
        try {
            // Load the new page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();


            // Create a new stage for the new scene
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.TRANSPARENT);
            newStage.show();

            // Close the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void fnProfil(ActionEvent event) {
        pnprofile.toFront();
    }

    @FXML
    void handleBrowser(ActionEvent event) {

    }

    @FXML
    void openInterface(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userService = UserService.getInstance();
        pnprofile.toBack();
        User authenticatedUser = userService.getAuthenticatedUser();
        tfEmail.setText(authenticatedUser.getEmail());
        tfPrenom.setText(authenticatedUser.getPrenom());
        tfNom.setText(authenticatedUser.getNom());
        tfAdresse.setText(authenticatedUser.getZipcode()+", "+authenticatedUser.getVille()+", "+authenticatedUser.getAdress());

        tfEmailAdmin.setOnKeyReleased(this::validateEmail);
        tfConfirmEmailAdmin.setOnKeyReleased(this::validateConfirmEmail);
        tfPasswordAdmin.setOnKeyReleased(this::validatePassword);
        tfAdress.setOnKeyReleased(this::validateAddress);
        tfNomAdmin.setOnKeyReleased(this::validateNom);
        tfPrenomAmin.setOnKeyReleased(this::validatePrenom);
        lbUserNameGlobal.setText(authenticatedUser.getNom() + " " + authenticatedUser.getPrenom());
        fnReloadData();
    }

    private void fnReloadData(){
        // Associer les colonnes du tableau aux propriétés de l'objet User
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        SupprimerCol.setCellValueFactory(new PropertyValueFactory<>("blockButton"));
        familynameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        isVerifiedCol.setCellValueFactory(new PropertyValueFactory<>("verified"));

        // Charger les données depuis la base de données et les afficher dans le tableau

        tableViewUsers.setItems(FXCollections.observableArrayList(loadDataFromDatabase()));
        ObservableList<String> listTrier = FXCollections.observableArrayList("Name","Family name","Email");
        comboBox.setItems(listTrier);


        FilteredList<User> filtredData = new FilteredList<>(userList, e -> true);
        tfSearch.setOnKeyReleased(e -> {
            tfSearch.textProperty().addListener( (observableValue, oldValue, newValue) ->{
                filtredData.setPredicate((Predicate<? super User>) user ->{
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFiler = newValue.toLowerCase();
                    if(user.getEmail().contains(lowerCaseFiler)){
                        return true;
                    }else if(user.getNom().toLowerCase().contains(lowerCaseFiler)){
                        return true;
                    }else if(user.getPrenom().toLowerCase().contains(lowerCaseFiler)) {
                        return true;
                    } else if (user.getEmail() != null && user.getEmail().toString().toLowerCase().contains(lowerCaseFiler)) {
                        return true;
                    } else if (user.getAdress() != null && user.getAdress().toString().toLowerCase().contains(lowerCaseFiler)) {
                        return true;

                    }else {

                        return false;
                    }

                });
            });
            SortedList<User> sortedData = new SortedList<>(filtredData);
            sortedData.comparatorProperty().bind(tableViewUsers.comparatorProperty());
            tableViewUsers.setItems(sortedData);
        });

    }
    private List<User> loadDataFromDatabase() {
        List<User> data= new ArrayList<>();
        UserService us = new UserService();
        for(int i=  0 ; i<us.getAll().size();i++){
            System.out.println(us.getAll().get(i).toString());
            User user = us.getAll().get(i);
            if(user.isIs_verified())
                user.setVerified("Verified");
            else
                user.setVerified("Not Verified");
            blockButton = new Button();
            blockButton.setText("Block");
            blockButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            blockButton.setOnAction(e -> {
                    us.delete(user);
                    fnReloadData();
                });

            user.setBlockButton(blockButton);
            data.add(user);


        }
        return data;
    }

    void FnReloadDataFiltred(String filter){
        try (Connection connection = ConnectionManager.getConnection()) {
            String query = "SELECT * FROM `user` WHERE id != 1 ORDER BY "+filter+" ASC";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    User user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("password"),
                            resultSet.getString("email"),
                            resultSet.getString("prenom"),
                            resultSet.getString("adress"),
                            resultSet.getString("ville"),
                            resultSet.getInt("zipcode"),
                            resultSet.getString("roles"),
                            resultSet.getBoolean("is_verified")
                    );

                    if(user.isIs_verified())
                        user.setVerified("Verified");
                    else
                        user.setVerified("Not Verified");
                    UserService us = new UserService();
                    Button blockButton = new Button();
                    blockButton.setText("Block");
                    blockButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    blockButton.setOnAction(e -> {
                    us.delete(user);
                    fnReloadData();
                        });

                    user.setBlockButton(blockButton);
                    userList.add(user);
                }
            }
            tableViewUsers.setItems(userList);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    void Select(ActionEvent event) {
        userList.clear();
        if (comboBox.getSelectionModel().getSelectedItem().toString().equals("Name")) {

            FnReloadDataFiltred("prenom");

        } else if (comboBox.getSelectionModel().getSelectedItem().toString().equals("Family name")) {

            FnReloadDataFiltred("nom");

        } else if (comboBox.getSelectionModel().getSelectedItem().toString().equals("Email")) {
            FnReloadDataFiltred("email");
        }
    }




    @FXML
    void handleChart(ActionEvent event) throws IOException {
        // Load the FXML file for the new stage
        Parent root = FXMLLoader.load(getClass().getResource("/esprit/manettek/demo/Chart.fxml"));
        // Create the new stage
        Stage newStage = new Stage();
        // Set the title of the new stage
        newStage.setTitle("BarChart");
        // Create the scene for the new stage
        Scene scene = new Scene(root);
        newStage.setScene(scene);
        // Show the new stage
        newStage.show();

    }

    @FXML
    void ResetPasswordAction(ActionEvent event) throws Exception {
        userService = UserService.getInstance();
        User authenticatedUser = userService.getAuthenticatedUser();
        ResetPasswordService rps = new ResetPasswordService();
        authenticatedUser.setPassword(tfConfirmPassword.getText());
        rps.ResetPassword(authenticatedUser);
        tfPassword.setText("");
        tfConfirmPassword.setText("");
        showUpdate("Password Updated succesfully");

    }

    @FXML
    void changeProfilAction(ActionEvent event) {
        userService = UserService.getInstance();
        // Your existing initialization code...
        // Example usage:
        User authenticatedUser = userService.getAuthenticatedUser();
        authenticatedUser.setEmail(tfEmail.getText());
        authenticatedUser.setNom(tfNom.getText());
        authenticatedUser.setPrenom(tfPrenom.getText());
        String address = tfAdresse.getText();
        Pattern pattern = Pattern.compile("^(\\d{4}),\\s*([^,]+),\\s*(.+)$");
        Matcher matcher = pattern.matcher(address);
        if (matcher.matches()) {
            int zipCode = Integer.parseInt(matcher.group(1));
            String ville = matcher.group(2);
            String fullAddress = matcher.group(3);
            authenticatedUser.setZipcode(zipCode);
            authenticatedUser.setVille(ville);
            authenticatedUser.setAdress(fullAddress);
            userService.update(authenticatedUser);
            showUpdate("user infos updated succesfully");
            pnprofile.toBack();
        }else{
            showErrorAlert("check your information");
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle("Invalid Information");
        alert.setHeaderText(null);
        alert.show();
    }

    private void showUpdate(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        alert.setTitle("User Updated");
        alert.setHeaderText(null);
        alert.show();
    }

    @FXML
    void fnPaneAdmin(MouseEvent event) {
        pnAddAdmin.toFront();
    }

    @FXML
    void fnPaneUsers(MouseEvent event) {
        pnUsers.toFront();
        fnReloadData();
    }

    @FXML
    void fnUserName(MouseEvent event) {
        pnprofile.toFront();
    }
}
