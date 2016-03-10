package config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Consts {
	
	private Consts() {
	}
	
	//rozmiar bufora odczytu/zapisu
	public static final int BUFFER_SIZE = 8192;
	
	public static final  int SEQUENCE_ELEMENT_COUNT = 20;
	
	//nazwa pierwszego pliku do ktorego beda zapisane podane dane
	public static final String FIRST_FILE_NAME = "pierwszyZapis.dat";
	
	//nazwa folderu w ktorym beda pliki z poszczegolnymi fazami
	public static final String PHASE_DIR = "Faza x";
	
	//nazwa pliku z tasmami
	public static final String FIRST_TAPE_FILE_NAME = "Tasma1.dat";
	
	public static final String SECOND_TAPE_FILE_NAME = "Tasma2.dat";
	
	public static final String MERGE_TAPE_FILE_NAME = "ScaloneTasmy.dat";
	
	public static String MAIN_SORT_DIRECTORY_NAME = "Sortowanie" + getCurrentDateTime();
	
	private static String getCurrentDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
		return dateFormat.format(new Date());
	}
}
