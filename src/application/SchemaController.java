package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

import config.Consts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.GeometricSequence;
import logic.NaturalMergeSort;

public class SchemaController implements Initializable{
	
	//------------------------------------------------------------------
	//Podlaczenie kontrolek
	
	@FXML
	//dodaj wiersz do tabeli z danymi do posortowania
	private Button addRowButton;
	
	@FXML
	//tabela z danymi do posortowania
	private TableView<GeometricSequence> toSortTable;
	
	@FXML
	//kolumna z 1. wyrazami ciagu
	private TableColumn<GeometricSequence, Number> firstTermToSortColumn;
	
	@FXML
	//kolumna z ilorazami ciagu
	private TableColumn<GeometricSequence, Number> multiplierToSortColumn;
	
	@FXML
	//tabela z posortowanymi danymi
	private TableView<GeometricSequence> sortedTable;
	
	@FXML
	//1. wyrazy ciagu
	private TableColumn<GeometricSequence, Number> firstTermSortedColumn;
	
	@FXML
	//ilorazy ciagu
	private TableColumn<GeometricSequence, Number> multiplierSortedColumn;
	
	@FXML
	//etykieta zawierajaca liczbe faz sortowania
	private Label sortingPhasesCountLabel;
	
	@FXML
	//etykieta zaw. liczbe odczytow
	private Label pageReadsCountLabel;
	
	@FXML
	//etykieta zaw. liczbe zapisow
	private Label pageSavesCountLabel;
	
	@FXML
	//etykieta z liczb¹ rekordów
	private Label recordsCountLabel;
	
	@FXML
	//MenuItem odpowiedzialny za wczytywanie danych z pliku
	private MenuItem openFile;
	
	@FXML
	//dodaj losowe dane
	private MenuItem addRandomDataMenuItem;
	
	@FXML
	//wyczyszczenie danych
	private MenuItem resetData;
	
	@FXML
	//info o autorze
	private MenuItem aboutAuthor;
	
	@FXML
	//Menu do wyswietlania fazy
	private Menu showPhaseMenu;
	
	@FXML
	//TextField z 1. elementem ciagu
	private TextField firstTermTextField;
	
	@FXML
	//TextField z ilorazem ciagu
	private TextField multiplierTextField;
	
	//---------------------------------------------------------------------------
	//Zmienne lokalne
	
	//dane do posortowania
	private ObservableList<GeometricSequence> dataToSort = FXCollections.observableArrayList();
	
	//posortowane dane
	private ObservableList<GeometricSequence> sortedData = FXCollections.observableArrayList();
	
	//katalog sortowania
	private File sortDirectory;
	
	//katalog startowy dla sortowañ
	private File startDirectory;
	
	//---------------------------------------------------------------------------

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//przypisanie list z danymi do tabel
		getFirstTermToSortColumn().setCellValueFactory(cellData -> 
			cellData.getValue().getFirstTermProperty());
		getMultiplierToSortColumn().setCellValueFactory(cellData -> 
			cellData.getValue().getMultiplierProperty());
		
		getFirstTermSortedColumn().setCellValueFactory(cellData ->
			cellData.getValue().getFirstTermProperty());
		getMultiplierSortedColumn().setCellValueFactory(cellData ->
			cellData.getValue().getMultiplierProperty());
		
		getToSortTable().setItems(getDataToSort());
		getSortedTable().setItems(getSortedData());
		
		//dopiero po sortowaniu mozna zobaczyc fazy sortowania
		//dlatego menu zostanie wylaczone
		getShowPhaseMenu().setDisable(true);
	}
	
	public void getDataFromFile() throws IOException{
		FileChooser fileChooser = new FileChooser();
		ExtensionFilter extensionFilter = new ExtensionFilter("Plik tekstowy", "*.txt");
		fileChooser.getExtensionFilters().add(extensionFilter);
		
		File dataFile = fileChooser.showOpenDialog(null);
		if(dataFile != null) {
			getDataToSort().clear();
			try(BufferedReader reader = Files.newBufferedReader(dataFile.toPath())) {
				String line = "";
				while((line = reader.readLine()) != null) {
					//tablica z danymi do wpisania [0] -> pierwszy element ciagu
					//[1] -> iloczyn
					String[] lineElements = line.split(" ");
					getDataToSort().add(new GeometricSequence(Double.parseDouble(lineElements[0]),
							Double.parseDouble(lineElements[1])));
				}
			}
			getRecordsCountLabel().setText(Integer.toString(getDataToSort().size()));
		}
	}
	
	public void showInfoAboutAuthor() {
		try{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("aboutWindow.fxml"));
			Parent root = (Parent)fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//dodaje losowe dane do sortowania, z zakresu (-10, 10)
	public void addRandomData() {
		final int MIN = -10, MAX = 10;
		
		//poka¿ okno do wpisywania liczby losowych rekordow
		try{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("randomDataWindow.fxml"));
			Parent root = (Parent)fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < RandomDataWindow.getRecordCount(); i++) {
			//losowanie 2 liczb
			double newFirstTerm = MIN + (Math.random() * (MAX - MIN)),
					newMultiplier = MIN + (Math.random() * (MAX - MIN));
			getDataToSort().add(new GeometricSequence(newFirstTerm, newMultiplier));
		}
		getRecordsCountLabel().setText(Integer.toString(getDataToSort().size()));
	}

	public void addRecord() {
		//jesli zostaly wpisane wartosci sprobuj je dodac
		if(getFirstTermTextField().getText() != null && getMultiplierTextField().getText() != null &&
				!getFirstTermTextField().getText().isEmpty() && !getMultiplierTextField().getText().isEmpty()) {
			//pobierz dane z kontrolki
			String newFirstTermText = getFirstTermTextField().getText(),
				newMultiplierText = getMultiplierTextField().getText();
			try{
				double newFirstTerm = Double.parseDouble(newFirstTermText),
					newMultiplier = Double.parseDouble(newMultiplierText);
				//jesli udalo sie sparsowac teksty, znaczy ze sa poprawnymi wartosciami
				getDataToSort().add(new GeometricSequence(newFirstTerm, newMultiplier));
			} catch (NullPointerException | NumberFormatException e) {
				e.printStackTrace();
			}
			getRecordsCountLabel().setText(Integer.toString(getDataToSort().size()));
		}
	}
	
	public void reset() {
		getDataToSort().clear();
		getSortedData().clear();
		getShowPhaseMenu().setDisable(true);
		getShowPhaseMenu().getItems().clear();
		
		//wyczyszczenie etykiet
		getPageReadsCountLabel().setText("");
		getPageSavesCountLabel().setText("");
		getRecordsCountLabel().setText("");
		getSortingPhasesCountLabel().setText("");
	}
	
	public void startSort() {
		if(!getDataToSort().isEmpty()) {
			if(getStartDirectory() == null) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				setStartDirectory(directoryChooser.showDialog(null));
			}
			if(getStartDirectory() != null) {
				File sortDirectory = new File(getStartDirectory().getAbsolutePath() + "\\" + 
						Consts.MAIN_SORT_DIRECTORY_NAME);
				sortDirectory.mkdir();
				NaturalMergeSort sorting = new NaturalMergeSort(sortDirectory);
				try {
					List<GeometricSequence> sortedData = sorting.sort(getDataToSort());
					getSortedData().clear();
					getSortedData().addAll(sortedData);
				} catch (IOException e) {
					getSortingPhasesCountLabel().setText("B³¹d sortowania");
					e.printStackTrace();
				}
				
				setSortDirectory(sorting.getStartDirectory());
				getShowPhaseMenu().getItems().clear();
				//dodanie elementow menu do wyswietlania szczegolow wybranej fazy
				for(int i = 0; i < sorting.getPhases(); i++) {
					MenuItem showPhaseMenuItem = new MenuItem(Consts.PHASE_DIR.replace("x", Integer.toString(i + 1)));
					showPhaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {		
						@Override
						public void handle(ActionEvent event) {
							try{
								MenuItem mi = (MenuItem)event.getSource();
								showPhaseDetails(Integer.parseInt(mi.getText().replace("Faza ", "")));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					getShowPhaseMenu().getItems().add(showPhaseMenuItem);
				}
				getShowPhaseMenu().setDisable(false);
				
				//ustawienie etykiet z liczbami odczytow/zapisow/faz
				getPageReadsCountLabel().setText(Integer.toString(sorting.getPageReads()));
				getPageSavesCountLabel().setText(Integer.toString(sorting.getPageSaves()));
				getSortingPhasesCountLabel().setText(Integer.toString(sorting.getPhases()));
				
				getRecordsCountLabel().setText(getRecordsCountLabel().getText());
			}
		}
	}
	
	private void showPhaseDetails(int phaseNum) throws IOException {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("phaseDetails.fxml"));
					Parent root = (Parent)fxmlLoader.load();
					fxmlLoader.<PhaseDetails>getController().initData(getSortDirectory(), phaseNum);
					Stage stage = new Stage();
					stage.setScene(new Scene(root));
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.showAndWait();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.run();
	}
	
	private TableView<GeometricSequence> getToSortTable() {
		return toSortTable;
	}

	private TableView<GeometricSequence> getSortedTable() {
		return sortedTable;
	}

	private Label getSortingPhasesCountLabel() {
		return sortingPhasesCountLabel;
	}

	private Label getPageReadsCountLabel() {
		return pageReadsCountLabel;
	}

	private Label getPageSavesCountLabel() {
		return pageSavesCountLabel;
	}

	private ObservableList<GeometricSequence> getDataToSort() {
		return dataToSort;
	}

	private TableColumn<GeometricSequence, Number> getFirstTermToSortColumn() {
		return firstTermToSortColumn;
	}

	private TableColumn<GeometricSequence, Number> getMultiplierToSortColumn() {
		return multiplierToSortColumn;
	}

	private TableColumn<GeometricSequence, Number> getFirstTermSortedColumn() {
		return firstTermSortedColumn;
	}

	private TableColumn<GeometricSequence, Number> getMultiplierSortedColumn() {
		return multiplierSortedColumn;
	}


	private TextField getFirstTermTextField() {
		return firstTermTextField;
	}

	private TextField getMultiplierTextField() {
		return multiplierTextField;
	}

	private ObservableList<GeometricSequence> getSortedData() {
		return sortedData;
	}

	private Menu getShowPhaseMenu() {
		return showPhaseMenu;
	}

	private File getSortDirectory() {
		return sortDirectory;
	}

	private void setSortDirectory(File sortDirectory) {
		this.sortDirectory = sortDirectory;
	}

	private File getStartDirectory() {
		return startDirectory;
	}

	private void setStartDirectory(File startDirectory) {
		this.startDirectory = startDirectory;
	}

	private Label getRecordsCountLabel() {
		return recordsCountLabel;
	}
}
