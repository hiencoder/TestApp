package jp.co.marinax.fileplayer.utils;

import java.security.InvalidAlgorithmParameterException;

public class EncodeAndDecodeUtils {

	// Decrypt data
	public static byte[] decrypt(String key, byte[] data) throws InvalidAlgorithmParameterException {

		byte[] keyBytes = key.getBytes();
		BlowfishECB bfe = new BlowfishECB(keyBytes, 0, keyBytes.length);
		byte[] result = new byte[data.length];
		bfe.decrypt(data, 0, result, 0, data.length);
		return result;
	}

}
