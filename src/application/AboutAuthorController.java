package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AboutAuthorController {
	//okno z informacjami o autorze

	@FXML
	private Button aboutAuthorButton;
	
	@FXML
	//wcisniecie OK
	private void confirmPopup(){
		Stage stage = (Stage)getAboutAuthorButton().getScene().getWindow();
		stage.close();
	}

	private Button getAboutAuthorButton() {
		return aboutAuthorButton;
	}
}
