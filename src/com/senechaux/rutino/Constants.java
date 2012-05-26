package com.senechaux.rutino;

public class Constants {

	public static final int ACTIVITY_CREATE_WALLET = 0;
	public static final int ACTIVITY_EDIT_WALLET = 1;

	/**
	 * Account type string.
	 */
	public static final String ACCOUNT_TYPE = "com.senechaux.rutino.account";

	/**
	 * Authtoken type string.
	 */
	public static final String AUTHTOKEN_TYPE = "com.senechaux.rutino";

	public static final String PREFIX_GLOBAL_ID = "m_";

	public static final String BASE_URL = "http://192.168.1.129/api.php";
	// "http://samplesyncadapter2.appspot.com";
	public static final String AUTH_URI = BASE_URL + "/auth";
	public static final String GET_WALLET_LIST = BASE_URL + "/wallet.json";
	public static final String GET_ACCOUNT_LIST = BASE_URL + "/account.json";
	public static final String GET_TRANSACTION_LIST = BASE_URL + "/transaction.json";
	public static final String GET_PERIODIC_TRANSACTION_LIST = BASE_URL + "/periodic_transaction.json";
	public static final String GET_REPORT_LIST = BASE_URL + "/report.json";
	public static final String GET_ACCOUNTTYPE_LIST = BASE_URL + "/account_type.json";
	public static final String GET_CURRENCY_LIST = BASE_URL + "/currency.json";
}
