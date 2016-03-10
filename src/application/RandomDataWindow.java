package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RandomDataWindow {
	
	@FXML
	//przycisk Anuluj
	private Button cancelRandomDataCountButton;
	
	@FXML
	//textField z liczba rekordow do dodania
	private TextField randomDataCountTextField;
	
	private static int recordCount;
	
	public void confirmRecordCount(){
		if(getRandomDataCountTextField().getText() != null && 
				!getRandomDataCountTextField().getText().isEmpty()){
			String recordCountText = getRandomDataCountTextField().getText();
			try{
				recordCount = Integer.parseInt(recordCountText);
				if(recordCount > 0){
					cancelAndExit();
				} else {
					recordCount = 0;
				}
			} catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
	}
	
	public void cancelAndExit(){
		Stage stage = (Stage)getCancelRandomDataCountButton().getScene().getWindow();
		stage.close();
	}

	private Button getCancelRandomDataCountButton() {
		return cancelRandomDataCountButton;
	}

	private TextField getRandomDataCountTextField() {
		return randomDataCountTextField;
	}

	public static int getRecordCount() {
		return recordCount;
	}
}
