package buffers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import config.Consts;
import logic.GeometricSequence;

public class ReadBuffer {
	
	private byte[] buffer;
	private GeometricSequence value;
	private FileInputStream tape;
	private int bufferPosition;
	private int readedBytes;
	private GeometricSequence prevValue;
	private boolean newRun;
	private boolean endOfFile;
	private int readsCount;
	private int runsCount;
	
	public ReadBuffer(File tape) {
		try {
			this.buffer = new byte[Consts.BUFFER_SIZE];
			this.tape = new FileInputStream(tape);
			this.readedBytes = this.tape.read(this.buffer);
			this.bufferPosition = 0;
			this.newRun = false;
			this.readsCount = 1;
			this.runsCount = 1;
			this.endOfFile = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * czyta z bufora nastepna wartosc i zwraca true jesli nalezy ona do tej samej serii co poprzednia
	 * pobiera wartosc z taœmy do pola value
	 */
	public void readNextValue() {
		if(getBufferPosition() >= getReadedBytes()) {
			if(getBufferPosition() < Consts.BUFFER_SIZE) {
				//jesli jestesmy na koncu bufora i jest w nim mniej danych niz max. rozmiar bufora
				setEndOfFile(true);
				return;
			}
			try {
				setReadedBytes(getTape().read(getBuffer()));
				setBufferPosition(0);
				setReadsCount(getReadsCount() + 1);
				if(getReadedBytes() == -1) {	//nic nie odczytano
					setEndOfFile(true);
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//pobierz wartosc z bufora
		getValueFromBuffer();
	}
	
	/**
	 * pobiera nastepna wartosc z bufora
	 */
	public void getValueFromBuffer() {
		byte[] firstTermBytes = new byte[8],
				multiplierBytes = new byte[8];
		for(int i = 0; i < 8; i++) {
			firstTermBytes[i] = getBuffer()[getBufferPosition() + i];
			multiplierBytes[i] = getBuffer()[getBufferPosition() + i + 8];
		}
		//przesuwamy pozycje w tablicy o rozmiar 2 doubli
		setBufferPosition(getBufferPosition() + 16);
		GeometricSequence gs = new GeometricSequence(ByteBuffer.wrap(firstTermBytes).getDouble(),
				ByteBuffer.wrap(multiplierBytes).getDouble());
		setPrevValue(getValue());
		setValue(gs);
		if( getPrevValue() != null && getValue() != null && 
				getPrevValue().getSum() > getValue().getSum()) {
			setNewRun(true);
			setRunsCount(getRunsCount() + 1);
		} else {
			setNewRun(false);
		}
	}
	
	public void close() {
		try {
			getTape().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isEmpty(){
		if(getReadedBytes() == 0 || getReadedBytes() == -1) {
			return true;
		}
		return false;
	}
	
	private byte[] getBuffer() {
		return buffer;
	}

	public GeometricSequence getValue() {
		return value;
	}

	private void setValue(GeometricSequence value) {
		this.value = value;
	}

	private int getBufferPosition() {
		return bufferPosition;
	}

	private void setBufferPosition(int bufferPosition) {
		this.bufferPosition = bufferPosition;
	}

	private int getReadedBytes() {
		return readedBytes;
	}

	private void setReadedBytes(int readedBytes) {
		this.readedBytes = readedBytes;
	}

	private FileInputStream getTape() {
		return tape;
	}

	private GeometricSequence getPrevValue() {
		return prevValue;
	}

	private void setPrevValue(GeometricSequence prevValue) {
		this.prevValue = prevValue;
	}

	public boolean isNewRun() {
		return newRun;
	}

	public void setNewRun(boolean newRun) {
		this.newRun = newRun;
	}

	public int getReadsCount() {
		return readsCount;
	}

	private void setReadsCount(int runCount) {
		this.readsCount = runCount;
	}

	public int getRunsCount() {
		return runsCount;
	}

	private void setRunsCount(int runsCount) {
		this.runsCount = runsCount;
	}

	public boolean isEndOfFile() {
		return endOfFile;
	}

	private void setEndOfFile(boolean endOfFile) {
		this.endOfFile = endOfFile;
	}
}
