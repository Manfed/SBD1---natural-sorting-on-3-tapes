package logic;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import buffers.ReadBuffer;
import buffers.WriteBuffer;
import config.Consts;

public class NaturalMergeSort {
	
	//folder poczatkowy
	private File startDirectory;
	//plik z danymi
	private File dataFile;
	//tasmy danych
	private File firstTape;
	private File secondTape;
	//folder fazy
	private File phaseDirectory;
	
	//zapisy stron
	private int pageSaves;
	//odczyty stron
	private int pageReads;
	//fazy sortowania
	private int phases;
	
	//czy dane sa juz posortowane
	private boolean sortEnd;
	
	/**
	 * konstruktor zapisujacy dane podane przez uzytkownika do pliku binarnego
	 * @param startDir
	 * @param dataToSort
	 * @throws FileNotFoundException
	 */
	public NaturalMergeSort(File startDir) {
		this.startDirectory = startDir;
		this.pageReads = 0;
		this.pageSaves = 0;
		this.phases = 0;
		this.sortEnd = false;
	}
	
	public List<GeometricSequence> sort(List<GeometricSequence> dataToSort) throws IOException{
		this.dataFile = new File(getStartDirectory().getAbsolutePath() + '\\' + Consts.FIRST_FILE_NAME);
		
		//zapis danych do pliku binarnego
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(dataFile));
		for(GeometricSequence gs : dataToSort) {
			try {
				dos.writeDouble(gs.getFirstTerm());
				dos.writeDouble(gs.getMultiplier());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dos.close();
		
		//sortowanie
		while(!isSortEnd()) {
			splitRuns();
			mergeRuns();
			setPhases(getPhases() + 1);
		}
		
		List<GeometricSequence> sortedData = getDataFromFile(getDataFile());
		return sortedData;
	}
	
	/**
	 * ³¹czenie taœm
	 */
	private void mergeRuns() {
		//utworzenie taœmy ze scalonymi danymi
		setDataFile(new File(getPhaseDirectory() + "\\" + Consts.MERGE_TAPE_FILE_NAME));
		
		//utworzenie buforów zapisu/odczytu
		ReadBuffer firstTapeReadBuffer = new ReadBuffer(getFirstTape());
		ReadBuffer secondTapeReadBuffer = new ReadBuffer(getSecondTape());
		WriteBuffer mergeTapeWriteBuffer = new WriteBuffer(getDataFile());
		Boolean readFromFirstTape = true,	//z ktorej tasmy ma odbywac sie odczyt
				readFromSecondTape = true;
		while(!firstTapeReadBuffer.isEndOfFile() || !secondTapeReadBuffer.isEndOfFile()) {
			//odczyt wartoœci z taœm
			if(readFromFirstTape) {
				firstTapeReadBuffer.readNextValue();
			}
			if(readFromSecondTape) {
				secondTapeReadBuffer.readNextValue();
			}
			
			readFromSecondTape = checkRunEnd(firstTapeReadBuffer, secondTapeReadBuffer, mergeTapeWriteBuffer);
			readFromFirstTape = checkRunEnd(secondTapeReadBuffer, firstTapeReadBuffer, mergeTapeWriteBuffer);
			
			if(((firstTapeReadBuffer.isNewRun() && secondTapeReadBuffer.isNewRun()) 
					|| (!firstTapeReadBuffer.isNewRun() && !secondTapeReadBuffer.isNewRun()))
					&& (!firstTapeReadBuffer.isEndOfFile() && !secondTapeReadBuffer.isEndOfFile())) {
				if(firstTapeReadBuffer.getValue().getSum() > secondTapeReadBuffer.getValue().getSum()) {
					mergeTapeWriteBuffer.write(secondTapeReadBuffer.getValue());
					readFromFirstTape = false;
					firstTapeReadBuffer.setNewRun(false);
					readFromSecondTape = true;
				} else {
					mergeTapeWriteBuffer.write(firstTapeReadBuffer.getValue());
					readFromFirstTape = true;
					readFromSecondTape = false;
					secondTapeReadBuffer.setNewRun(false);
				}
			}
		}

		//zamkniecie buforow
		firstTapeReadBuffer.close();
		secondTapeReadBuffer.close();
		mergeTapeWriteBuffer.close();
		
		//aktualizacja licznikow
		setPageReads(getPageReads() + firstTapeReadBuffer.getReadsCount() 
			+ secondTapeReadBuffer.getReadsCount());
		setPageSaves(getPageSaves() + mergeTapeWriteBuffer.getSavesCount());
		//jesli na kazdej tasmie byla 1 seria -> koniec sortowania
		if((firstTapeReadBuffer.getRunsCount() == 1 && secondTapeReadBuffer.getRunsCount() == 1)
				|| (firstTapeReadBuffer.isEmpty() || secondTapeReadBuffer.isEmpty())) {
			setSortEnd(true);
		}
	}
	
	/**
	 * Sprawdzenie czy skonczyla sie seria lub tasma
	 * @param tapeToCheck
	 * @param secondTape
	 * @param mergeTape
	 * @return	true jesli sprawdzana tasma ma byc dalej odczytywana
	 */
	private boolean checkRunEnd(ReadBuffer tapeToCheck, ReadBuffer secondTape, WriteBuffer mergeTape) {
		if (tapeToCheck.isNewRun() || tapeToCheck.isEndOfFile()) {
			while(!secondTape.isNewRun() && !secondTape.isEndOfFile()) {
				mergeTape.write(secondTape.getValue());
				secondTape.readNextValue();
			}
			if(secondTape.isEndOfFile()) {
				while (!tapeToCheck.isEndOfFile()) {
					mergeTape.write(tapeToCheck.getValue());
					tapeToCheck.readNextValue();
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * dystrybucja taœmy z danymi na 2 taœmy
	 */
	private void splitRuns() {
		if(!isSortEnd()) {
			//utworzenie folderu fazy
			setPhaseDirectory(new File(getStartDirectory().getAbsolutePath() + "\\" + 
					Consts.PHASE_DIR.replace("x", Integer.toString(getPhases() + 1))));
			//true -> zapis na 1. false -> 2. taœmê
			boolean writeToFirstTape = true;
			//utworzenie folderu z plikami fazy
			getPhaseDirectory().mkdir();
			//stworzenie plikow taœm
			setFirstTape(new File(getPhaseDirectory().getAbsolutePath() + "\\" +
					Consts.FIRST_TAPE_FILE_NAME));
			setSecondTape(new File(getPhaseDirectory().getAbsolutePath() + "\\" +
					Consts.SECOND_TAPE_FILE_NAME));
			
			ReadBuffer readBuffer = new ReadBuffer(getDataFile());
			WriteBuffer firstTapeWriter = new WriteBuffer(getFirstTape());
			WriteBuffer secondTapeWriter = new WriteBuffer(getSecondTape());
			readBuffer.readNextValue();
			
			while(!readBuffer.isEndOfFile()) {
				if(readBuffer.isNewRun()) {
					writeToFirstTape = !writeToFirstTape;
				}
				if(writeToFirstTape) {
					firstTapeWriter.write(readBuffer.getValue());
				} else {
					secondTapeWriter.write(readBuffer.getValue());
				}
				readBuffer.readNextValue();
			}
			//zamkniecie buforów
			readBuffer.close();
			firstTapeWriter.close();
			secondTapeWriter.close();
			
			setPageReads(getPageReads() + readBuffer.getReadsCount());
			setPageSaves(getPageSaves() + firstTapeWriter.getSavesCount() 
				+ secondTapeWriter.getSavesCount());
		}
	}

	private List<GeometricSequence> getDataFromFile(File dataFile) {
		ReadBuffer readBuffer = new ReadBuffer(dataFile);
		List<GeometricSequence> readedData = new ArrayList<>();
		
		readBuffer.readNextValue();
		while(!readBuffer.isEndOfFile()) {
			readedData.add(readBuffer.getValue());
			readBuffer.readNextValue();
		}
		return readedData;
	}
	
	public File getStartDirectory() {
		return startDirectory;
	}

	public void setStartDirectory(File startDirectory) {
		this.startDirectory = startDirectory;
	}

	public File getDataFile() {
		return dataFile;
	}

	public void setFirstDataSave(File firstDataSave) {
		this.dataFile = firstDataSave;
	}

	public int getPageSaves() {
		return pageSaves;
	}

	private void setPageSaves(int pageSaves) {
		this.pageSaves = pageSaves;
	}

	public int getPageReads() {
		return pageReads;
	}

	private void setPageReads(int pageReads) {
		this.pageReads = pageReads;
	}

	public int getPhases() {
		return phases;
	}

	private void setPhases(int phases) {
		this.phases = phases;
	}

	private File getFirstTape() {
		return firstTape;
	}

	private void setFirstTape(File firstTape) {
		this.firstTape = firstTape;
	}

	private File getSecondTape() {
		return secondTape;
	}

	private void setSecondTape(File secondTape) {
		this.secondTape = secondTape;
	}

	private File getPhaseDirectory() {
		return phaseDirectory;
	}

	private void setPhaseDirectory(File phaseDirectory) {
		this.phaseDirectory = phaseDirectory;
	}

	private void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}

	private boolean isSortEnd() {
		return sortEnd;
	}

	private void setSortEnd(boolean sortEnd) {
		this.sortEnd = sortEnd;
	}
}
