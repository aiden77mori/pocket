package com.droidoxy.easymoneyrewards.constants;

import com.droidoxy.easymoneyrewards.app.App;

import static com.droidoxy.easymoneyrewards.Config.SERVER_URL;

public interface Constants {

    String CLIENT_ID = "1";

    String API_DOMAIN = SERVER_URL;

    String API_FILE_EXTENSION = ".php";
    String API_VERSION = "v3";

    String OFFER_WALL_URL = "OFFER_WALL_URL";
    String API_DOMAIN_IMAGES = API_DOMAIN+"admin/images/";

    String METHOD_ACCOUNT_LOGIN = API_DOMAIN + "admin/api/" + API_VERSION + "/account.signIn" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SIGNUP = API_DOMAIN + "admin/api/" + API_VERSION + "/account.signUp" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_RECOVERY = API_DOMAIN + "admin/api/" + API_VERSION + "/account.recovery" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_AUTHORIZE = API_DOMAIN + "admin/api/" + API_VERSION + "/account.authorize" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_LOGOUT = API_DOMAIN + "admin/api/" + API_VERSION + "/account.logOut" + API_FILE_EXTENSION;

    String APP_PAYOUTS = API_DOMAIN + "admin/api/" + API_VERSION + "/app.Payouts" + API_FILE_EXTENSION;
    String ACCOUNT_REFER = API_DOMAIN + "admin/api/" + API_VERSION + "/account.Refer" + API_FILE_EXTENSION;
    String ACCOUNT_VERIFY = API_DOMAIN + "admin/api/" + API_VERSION + "/account.Verify" + API_FILE_EXTENSION;
    String APP_OFFERWALLS = API_DOMAIN + "admin/api/" + API_VERSION + "/app.OfferWalls" + API_FILE_EXTENSION;
    String ACCOUNT_REDEEM = API_DOMAIN + "admin/api/" + API_VERSION + "/account.Redeem" + API_FILE_EXTENSION;
    String ACCOUNT_REWARD = API_DOMAIN + "admin/api/" + API_VERSION + "/account.Reward" + API_FILE_EXTENSION;
    String ACCOUNT_BALANCE = API_DOMAIN + "admin/api/" + API_VERSION + "/account.Balance" + API_FILE_EXTENSION;
    String ACCOUNT_CHECKIN = API_DOMAIN + "admin/api/" + API_VERSION + "/account.Checkin" + API_FILE_EXTENSION;
    String ACCOUNT_SPIN = API_DOMAIN + "admin/api/" + API_VERSION + "/account.Spin" + API_FILE_EXTENSION;
    String APP_OFFERSTATUS = API_DOMAIN + "admin/api/" + API_VERSION + "/app.OfferStatus" + API_FILE_EXTENSION;
    String ACCOUNT_TRANSACTIONS = API_DOMAIN + "admin/api/" + API_VERSION + "/account.Transactions" + API_FILE_EXTENSION;

    String APP_VIDEOS = API_DOMAIN + "admin/api/" + API_VERSION + "/app.Videos" + API_FILE_EXTENSION;
    String APP_VIDEOSTATUS = API_DOMAIN + "admin/api/" + API_VERSION + "/app.VideoStatus" + API_FILE_EXTENSION;

    String API_ADMANTUM = API_DOMAIN + "admin/api/" + API_VERSION + "/api.AdMantum" + API_FILE_EXTENSION;

    int ERROR_SUCCESS = 0;

    int ERROR_LOGIN_TAKEN = 300;
    int ERROR_EMAIL_TAKEN = 301;
    int ERROR_IP_TAKEN = 302;

    int ACCOUNT_STATE_ENABLED = 0;
    int ACCOUNT_STATE_DISABLED = 1;
    int ACCOUNT_STATE_BLOCKED = 2;
    int ACCOUNT_STATE_DEACTIVATED = 3;

    String AdMantumActive = "AdMantumActive";
    String AdMantumPubId = "AdMantumPubId";
    String AdMantumAppId = "AdMantumAppId";
    String AdMantumOffersType = "AdMantumOffersType";
    String ADMANTUM_GOT_RESPONSE = "ADMANTUM_GOT_RESPONSE";
    String ADMANTUM_RESPONSE = "ADMANTUM_RESPONSE";

    String KiwiWallActive = "KiwiWallActive";
    String KiwiWallWallId = "KiwiWall_WallId";
    String KiwiWallAPIKEY = "KiwiWall_APIKEY";

    String OfferDaddy_AppId = "OfferDaddy_AppId";
    String OfferDaddy_AppKey = "OfferDaddy_AppKey";

    // App prefs
    String VERIFY_EMAIL_ACTIVE = "VERIFY_EMAIL_ACTIVE";


    // Offers Type
    String WallOnly = "wall_only";
    String Both = "both";
    String ApiOnly = "api_only";

    int ERROR_UNKNOWN = 100;
    int ERROR_ACCESS_TOKEN = 101;

    int ERROR_ACCOUNT_ID = 400;

    Boolean DEBUG_MODE = App.getInstance().get("APP_DEBUG_MODE",false);

    String LICENSE_COPY = "http://www.codyhub.com/item/android-rewards-app-pocket/";
}
