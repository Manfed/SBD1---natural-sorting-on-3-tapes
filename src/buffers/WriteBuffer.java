package buffers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import config.Consts;
import logic.GeometricSequence;

public class WriteBuffer {
	
	//plik do zapisu
	private FileOutputStream tape;
	//bufor zapisu
	private byte[] buffer;
	//pozycja w buforze
	private int bufferPosition;
	//ilosc zapisow
	private int savesCount;
	
	public WriteBuffer(File tape) {
		try {
			this.tape = new FileOutputStream(tape);
			this.buffer = new byte[Consts.BUFFER_SIZE];
			this.bufferPosition = 0;
			this.savesCount = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void write(GeometricSequence gsElement) {
		//jesli bufor jest pelen - zapisz do pliku
		if(getBufferPosition() + 16 > Consts.BUFFER_SIZE) {
			try {
				//zapis bufora do pliku
				getTape().write(getBuffer());
				setSavesCount(getSavesCount() + 1);
				//wyczyszczenie bufora
				setBuffer(new byte[Consts.BUFFER_SIZE]);
				setBufferPosition(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//tablice do zamiany rekordow na bajty
		byte[] firstTermBytes = new byte[8],
				multiplierBytes = new byte[8];
		ByteBuffer.wrap(firstTermBytes).putDouble(gsElement.getFirstTerm());
		ByteBuffer.wrap(multiplierBytes).putDouble(gsElement.getMultiplier());
		//zapisz do bufora
		insertInBuffer(firstTermBytes);
		insertInBuffer(multiplierBytes);
	}
	
	/**
	 * zapisz bufor do pliku i zamknij plik
	 */
	public void close() {
		try {
			if(getBufferPosition() > 0) {
				getTape().write(getBuffer(), 0, getBufferPosition());
				setSavesCount(getSavesCount() + 1);
			}
			getTape().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void insertInBuffer(byte[] value) {
		for(int i = 0; i < 8; i++) {
			getBuffer()[getBufferPosition() + i] = value[i];
		}
		setBufferPosition(getBufferPosition() + 8);
	}

	private byte[] getBuffer() {
		return buffer;
	}

	private void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	private int getBufferPosition() {
		return bufferPosition;
	}

	private void setBufferPosition(int bufferPosition) {
		this.bufferPosition = bufferPosition;
	}

	private FileOutputStream getTape() {
		return tape;
	}

	public int getSavesCount() {
		return savesCount;
	}

	private void setSavesCount(int savesCount) {
		this.savesCount = savesCount;
	}
}
