package application;

import java.io.File;

import buffers.ReadBuffer;
import config.Consts;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logic.GeometricSequence;

public class PhaseDetails {

	@FXML
	private TableView<GeometricSequence> phase1TableView;
	
	@FXML
	private TableColumn<GeometricSequence, Number> p1FirstTermColumn;
	
	@FXML
	private TableColumn<GeometricSequence, Number> p1MultiplierColumn;
	
	@FXML
	private TableColumn<GeometricSequence, Number> p1SumColumn;
	
	@FXML
	private TableView<GeometricSequence> phase2TableView;
	
	@FXML
	private TableColumn<GeometricSequence, Number> p2FirstTermColumn;
	
	@FXML
	private TableColumn<GeometricSequence, Number> p2MultiplierColumn;
	
	@FXML
	private TableColumn<GeometricSequence, Number> p2SumColumn;
	
	@FXML 
	private TableView<GeometricSequence> mergeTableView;
	
	@FXML
	private TableColumn<GeometricSequence, Number> mergeFirstTermColumn;
	
	@FXML
	private TableColumn<GeometricSequence, Number> mergeMultiplierColumn;
	
	@FXML
	private TableColumn<GeometricSequence, Number> mergeSumColumn;
	
	@FXML
	private Button closeButton;
	
	//-----------------------------------------------------------------------
	
	private ObservableList<GeometricSequence> firstTapeData = FXCollections.observableArrayList();
	
	private ObservableList<GeometricSequence> secondTapeData = FXCollections.observableArrayList();
	
	private ObservableList<GeometricSequence> mergeTapeData = FXCollections.observableArrayList();
	
	//-----------------------------------------------------------------------

	private TableView<GeometricSequence> getPhase1TableView() {
		return phase1TableView;
	}

	private TableColumn<GeometricSequence, Number> getP1FirstTermColumn() {
		return p1FirstTermColumn;
	}

	private TableColumn<GeometricSequence, Number> getP1MultiplierColumn() {
		return p1MultiplierColumn;
	}

	private TableColumn<GeometricSequence, Number> getP1SumColumn() {
		return p1SumColumn;
	}

	private TableView<GeometricSequence> getPhase2TableView() {
		return phase2TableView;
	}

	private TableColumn<GeometricSequence, Number> getP2FirstTermColumn() {
		return p2FirstTermColumn;
	}

	private TableColumn<GeometricSequence, Number> getP2MultiplierColumn() {
		return p2MultiplierColumn;
	}

	private TableColumn<GeometricSequence, Number> getP2SumColumn() {
		return p2SumColumn;
	}

	private TableView<GeometricSequence> getMergeTableView() {
		return mergeTableView;
	}

	private TableColumn<GeometricSequence, Number> getMergeFirstTermColumn() {
		return mergeFirstTermColumn;
	}

	private TableColumn<GeometricSequence, Number> getMergeMultiplierColumn() {
		return mergeMultiplierColumn;
	}

	private TableColumn<GeometricSequence, Number> getMergeSumColumn() {
		return mergeSumColumn;
	}

	private Button getCloseButton() {
		return closeButton;
	}

	private ObservableList<GeometricSequence> getFirstTapeData() {
		return firstTapeData;
	}

	private ObservableList<GeometricSequence> getSecondTapeData() {
		return secondTapeData;
	}

	private ObservableList<GeometricSequence> getMergeTapeData() {
		return mergeTapeData;
	}
	
	public void initData(File sortDir, int phaseNumber) {
		
		//powiazanie danych z tabelami
		getP1FirstTermColumn().setCellValueFactory(cellData -> cellData.getValue().getFirstTermProperty());
		getP1MultiplierColumn().setCellValueFactory(cellData -> cellData.getValue().getMultiplierProperty());
		getP1SumColumn().setCellValueFactory(cellData -> new SimpleDoubleProperty(
				cellData.getValue().getSum()));
		//2. tabela
		getP2FirstTermColumn().setCellValueFactory(cellData -> cellData.getValue().getFirstTermProperty());
		getP2MultiplierColumn().setCellValueFactory(cellData -> cellData.getValue().getMultiplierProperty());
		getP2SumColumn().setCellValueFactory(cellData -> new SimpleDoubleProperty(
				cellData.getValue().getSum()));
		//3. tabela
		getMergeFirstTermColumn().setCellValueFactory(cellData -> cellData.getValue().getFirstTermProperty());
		getMergeMultiplierColumn().setCellValueFactory(cellData -> cellData.getValue().getMultiplierProperty());
		getMergeSumColumn().setCellValueFactory(cellData -> new SimpleDoubleProperty(
				cellData.getValue().getSum()));
		
		String phasePath = sortDir.getAbsolutePath() + "\\" + Consts.PHASE_DIR.replace("x", 
				Integer.toString(phaseNumber));
		File firstTape = new File(phasePath + "\\" + Consts.FIRST_TAPE_FILE_NAME);
		File secondTape = new File(phasePath + "\\" + Consts.SECOND_TAPE_FILE_NAME);
		File mergeTape = new File(phasePath + "\\" + Consts.MERGE_TAPE_FILE_NAME);
		
		ReadBuffer firstTapeReadBuffer = new ReadBuffer(firstTape);
		ReadBuffer secondTapeReadBuffer = new ReadBuffer(secondTape);
		ReadBuffer mergeTapeReadBuffer = new ReadBuffer(mergeTape);
		//odczyt pierwszych wartosci
		firstTapeReadBuffer.readNextValue();
		secondTapeReadBuffer.readNextValue();
		mergeTapeReadBuffer.readNextValue();
		while(!firstTapeReadBuffer.isEndOfFile() || !secondTapeReadBuffer.isEndOfFile() 
				|| !mergeTapeReadBuffer.isEndOfFile()) {
			if(!firstTapeReadBuffer.isEndOfFile()){
				getFirstTapeData().add(firstTapeReadBuffer.getValue());
				firstTapeReadBuffer.readNextValue();
			}
			if(!secondTapeReadBuffer.isEndOfFile()) {
				getSecondTapeData().add(secondTapeReadBuffer.getValue());
				secondTapeReadBuffer.readNextValue();
			}
			if(!mergeTapeReadBuffer.isEndOfFile()) {
				getMergeTapeData().add(mergeTapeReadBuffer.getValue());
				mergeTapeReadBuffer.readNextValue();
			}
		}
		getPhase1TableView().setItems(getFirstTapeData());
		getPhase2TableView().setItems(getSecondTapeData());
		getMergeTableView().setItems(getMergeTapeData());
	}
	
	public void close() {
		((Stage)getCloseButton().getScene().getWindow()).close();
	}
}
