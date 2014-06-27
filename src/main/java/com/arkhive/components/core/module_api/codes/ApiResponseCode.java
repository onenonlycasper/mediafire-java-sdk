package com.arkhive.components.core.module_api.codes;

/**
 * enum for all response codes for any API response root/result. (List received from Cliff)
 */
public enum ApiResponseCode {
    NO_ERROR(0),
    ERROR_DIRECT_LINK_INTERNAL_ERROR(40),
    ERROR_DIRECT_LINK_ACCOUNT_SUSPENDED(41),
    ERROR_DIRECT_LINK_DMCA_BLOCKED(42),
    ERROR_DIRECT_LINK_DMCA_THROTTLE_LIMIT_REACHED(43),
    ERROR_INTERNAL(100),
    ERROR_MISSING_KEY(102),
    ERROR_INVALID_KEY(103),
    ERROR_MISSING_TOKEN(104),
    ERROR_INVALID_TOKEN(105),
    ERROR_CHANGE_EXTENSION(106),
    ERROR_INVALID_CREDENTIALS(107),
    ERROR_INVALID_USER(108),
    ERROR_INVALID_APPID(109),
    ERROR_INVALID_QUICKKEY(110),
    ERROR_MISSING_QUICKKEY(111),
    ERROR_INVALID_FOLDERKEY(112),
    ERROR_MISSING_FOLDERKEY(113),
    ERROR_ACCESS_DENIED(114),
    ERROR_FOLDER_PATH_CONFLICT(115),
    ERROR_INVALID_DATE(116),
    ERROR_MISSING_FOLDERNAME(117),
    ERROR_INVALID_FILENAME(118),
    ERROR_NO_MF_EMAIL(119),
    ERROR_EMAIL_TAKEN(120),
    ERROR_EMAIL_REJECTED(121),
    ERROR_EMAIL_MISFORMATTED(122),
    ERROR_PASSWORD_MISFORMATTED(123),
    ERROR_API_VERSION_MISSING(124),
    ERROR_OLD_API_VERSION(125),
    ERROR_API_CALL_DEPRECATED(126),
    ERROR_INVALID_SIGNATURE(127),
    ERROR_MISSING_PARAMS(128),
    ERROR_INVALID_PARAMS(129),
    ERROR_NON_PRO_LIMIT_REACHED(130),
    ERROR_ADD_OWNED_FOLDER(131),
    ERROR_REMOVE_OWNED_FOLDER(132),
    ERROR_ADD_ANON_FOLDER(133),
    ERROR_NOT_DMCA_USER(134),
    ERROR_DMCA_IP_OUT_OF_RANGE(135),
    ERROR_DMCA_LIMIT_EXCEEDED(136),
    ERROR_CONTACT_ALREADY_EXISTS(137),
    ERROR_CONTACT_DOES_NOT_EXIST(138),
    ERROR_CONTACT_GROUP_EXISTS(139),
    ERROR_UNKNOWN_CONTACT_GROUP(140),
    ERROR_UNKNOWN_DEVICE(141),
    ERROR_INVALID_FILE_TYPE(142),
    ERROR_FILE_ALREADY_EXISTS(143),
    ERROR_FOLDER_ALREADY_EXISTS(144),
    ERROR_APPLICATION_DISABLED(145),
    ERROR_APPLICATION_SUSPENDED(146),
    ERROR_ZIP_MULTIPLE_OWNERS(147),
    ERROR_ZIP_NON_PRO_DOWNLOAD(148),
    ERROR_ZIP_OWNER_NOT_PRO(149),
    ERROR_ZIP_FILE_TOO_BIG(150),
    ERROR_ZIP_NO_FILES_SELECTED(151),
    ERROR_ZIP_NO_FILES_ZIPPED(152),
    ERROR_ZIP_TOTAL_SIZE_TOO_BIG(153),
    ERROR_ZIP_NUM_FILES_EXCEEDED(154),
    ERROR_ZIP_OWNER_INSUFFICIENT_BANDWIDTH(155),
    ERROR_ZIP_REQUESTER_INSUFFICIENT_BANDWIDTH(156),
    ERROR_ZIP_ALL_INSUFFICIENT_BANDWIDTH(157),
    ERROR_FILE_EXISTS(158),
    ERROR_FOLDER_EXISTS(159),
    ERROR_INVALID_ACCEPTANCE_TOKEN(160),
    ERROR_USER_MUST_ACCEPT_TOS(161),
    ERROR_LIMIT_EXCEEDED(162),
    ERROR_ACCESS_LIMIT_REACHED(163),
    ERROR_DMCA_ALREADY_REPORTED(164),
    ERROR_DMCA_ALREADY_REMOVED(165),
    ERROR_ADD_PRIVATE_FOLDER(166),
    ERROR_FOLDER_DEPTH_LIMIT(167),
    ERROR_INVALID_PRODUCT_ID(168),
    ERROR_UPLOAD_FAILED(169),
    ERROR_TARGET_PLAN_NOT_IN_THE_SAME_CLASS(170),
    ERROR_BIZ_PLAN_RESTRICTION(171),
    ERROR_EXPIRATION_DATE_RESTRICTION(172),
    ERROR_NOT_PREMIUM_USER(173),
    ERROR_INVALID_URL(174),
    ERROR_INVALID_UPLOAD_KEY(175),
    ERROR_STORAGE_LIMIT_RESTRICTION(176),
    ERROR_DUPLICATE_ENTRY(177),
    ERROR_PRODUCT_ID_MATCH(178),
    ERROR_NOT_CURRENT_PRODUCT(179),
    ERROR_BIZ_DOWNGRADE(180),
    ERROR_BUSINESS_UPGRADE(181),
    ERROR_CHANGE_PLAN_CREDIT(182),
    ERROR_BANDWIDTH_ERROR(183),
    ERROR_ALREADY_LINKED(184),
    ERROR_INVALID_FOLDERNAME(185),
    ERROR_ZIP_PASSWORD_BULK(186),
    ERROR_SERVER_NOT_FOUND(187),
    ERROR_NOT_LOGGED_IN(188),
    ERROR_RESELLER_TOS(189),
    ERROR_BUSINESS_SEAT(190),
    ERROR_BANNED_BUYER(191),
    ERROR_RESELLER_CREDITS_ERROR(192),
    ERROR_PURCHASE_BANNED_ERROR(193),
    ERROR_SUBDOMAIN_ERROR(194),
    ERROR_TOO_MANY_FAILED(195),
    ERROR_INVALID_CARD(196),
    ERROR_RECENT_SUBSCRIBER(197),
    ERROR_INVOICE_FAILED(198),
    ERROR_DUPLICATE_API_TRANSACTION(199),
    ERROR_CARDCCV_ERROR(200),
    ERROR_TRANSACTION_DECLINED(201),
    ERROR_PREPAID_CARD(202),
    ERROR_NO_RESPONSE(203),
    ERROR_HIGH_RISK(204),
    ERROR_DECLINED_HIGH_RISK(205),
    ERROR_CARD_STORE_FAILED(206),
    ERROR_COPY_LIMIT_EXCEEDED(207),
    ERROR_ASYNC_JOB_IN_PROGRESS(208),
    ERROR_FOLDER_ALREADY_DELETED(209),
    ERROR_FILE_ALREADY_DELETED(210),
    ERROR_CANT_MODIFY_DELETED_ITEMS(211),
    ERROR_CHANGE_FROM_FREE(212),
    ERROR_DMCA_CANNOT_REPORT(213),
    ERROR_INVALID_FILEDROP_KEY(214),
    ERROR_MISSING_SIGNATURE(215),
    ERROR_EMAIL_ADDRESS_TOO_SHORT(216),
    ERROR_EMAIL_ADDRESS_TOO_LONG(217),
    ERROR_FB_EMAIL_MISSING(218),
    ERROR_FB_EMAIL_EXISTS(219),
    ERROR_AUTH_FACEBOOK(220),
    ERROR_AUTH_TWITTER(221),
    ERROR_UNKNOWN_PATCH(222),
    ERROR_INVALID_REVISION(223),
    ERROR_NO_ACTIVE_INVOICE(224),
    ERROR_APPLICATION_NO_LOGGING(225),
    ERROR_INVALID_INSTALLATION_ID(226),
    ERROR_INCIDENT_MISMATCH(227),
    ERROR_MISSING_FACEBOOK_TOKEN(228),
    ERROR_MISSING_TWITTER_TOKEN(229),
    ERROR_NO_AVATAR(230),
    ERROR_INVALID_SOFTWARE_TOKEN(231),
    ERROR_EMAIL_NOT_VALIDATED(232),
    ERROR_AUTH_GMAIL(233),
    ERROR_FAILED_TO_SEND_MESSAGE(234),
    ERROR_USER_IS_OWNER(235),
    ERROR_USER_IS_FOLLOWER(236),
    ERROR_USER_NOT_FOLLOWER(237),
    ERROR_PATCH_NO_CHANGE(238),
    ERROR_SHARE_LIMIT_REACHED(239),
    ERROR_CANNOT_GRANT_PERMS(240),
    ERROR_INVALID_PRINT_SERVICE(241),
    ERROR_FOLDER_FILES_EXCEEDED(242),
    ERROR_ACCOUNT_TEMPORARILY_LOCKED(243),
    ERROR_NON_US_USER(244),;

    private final int value;

    private static final int ZIP_MAX_FILESIZE_READABLE = 0; //TODO(cnajar): get correct value from rabie
    private static final int ZIP_MAX_TOTAL_FILESIZE = 0; //TODO(cnajar): get correct value from rabie
    private static final int ZIP_MAX_FILES = 0; //TODO(cnajar): get correct value from rabie
    private static final int MAX_OBJECTS = 6000;
    private static final int FOLDER_DEPTH_LIMIT = 130;

    private ApiResponseCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ApiResponseCode fromInt(int value) {
        for (final ApiResponseCode e : values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String returnMessage;
        switch (value) {
            case 0:
                returnMessage = "Success";
                break;
            case 100:
                returnMessage = "Internal server error";
                break;
            case 102:
                returnMessage = "API Key is missing";
                break;
            case 103:
                returnMessage = "The supplied API Key is invalid";
                break;
            case 104:
                returnMessage = "Session token is missing";
                break;
            case 105:
                returnMessage = "The supplied Session Token is expired or invalid";
                break;
            case 106:
                returnMessage = "Unknown or invalid user";
                break;
            case 107:
                returnMessage = "The Credentials you entered are invalid";
                break;
            case 108:
                returnMessage = "Unknown or invalid user";
                break;
            case 109:
                returnMessage = "Unknown or invalid Application ID";
                break;
            case 110:
                returnMessage = "Unknown or Invalid QuickKey";
                break;
            case 111:
                returnMessage = "Quick Key is missing";
                break;
            case 112:
                returnMessage = "Unknown or invalid FolderKey";
                break;
            case 113:
                returnMessage = "Folder Key is missing";
                break;
            case 114:
                returnMessage = "Access denied";
                break;
            case 115:
                returnMessage = "Cannot move/copy a Folder to itself or to one of its Sub-Folders";
                break;
            case 116:
                returnMessage = "The date specified is not valid";
                break;
            case 117:
                returnMessage = "Folder Name is missing";
                break;
            case 118:
                returnMessage = "The File Name specified is invalid";
                break;
            case 119:
                returnMessage = "You cannot register a Mediafire.com email address";
                break;
            case 120:
                returnMessage = "The email address you specified is already in use";
                break;
            case 121:
                returnMessage = "The email address you specified is found to be rejected/bounced";
                break;
            case 122:
                returnMessage = "The email address you specified is malformed";
                break;
            case 123:
                returnMessage = "The password you specified is malformed";
                break;
            case 124:
                returnMessage = "The API Version is missing";
                break;
            case 125:
                returnMessage = "The API version specified or the API Library is old for this call. "
                        + "Please Specify a higher API version or update your API Library";
                break;
            case 126:
                returnMessage = "This API call has been deprecated for the API version specified or the API "
                        + "Library. Please refer to the API documentation for alternative calls";
                break;
            case 127:
                returnMessage = "The signature you specified is invalid";
                break;
            case 128:
                returnMessage = "Required parameters for this request are missing";
                break;
            case 129:
                returnMessage = "One or more parameters for this request are invalid";
                break;
            case 130:
                returnMessage = "Non premium account limitation reached";
                break;
            case 131:
                returnMessage = "Cannot add a shared folder to its owner's account";
                break;
            case 132:
                returnMessage = "Cannot remove a shared folder from its owner's account";
                break;
            case 133:
                returnMessage = "Cannot add a shared folder from an anonymous account";
                break;
            case 134:
                returnMessage = "Invalid DMCA user";
                break;
            case 135:
                returnMessage = "IP out of range for DMCA";
                break;
            case 136:
                returnMessage = "DMCA Ban limit exceeded";
                break;
            case 137:
                returnMessage = "This contact already exists in the user's contact list";
                break;
            case 138:
                returnMessage = "This contact does not exist";
                break;
            case 139:
                returnMessage = "This group already exists in the user's contact list";
                break;
            case 140:
                returnMessage = "This group does not exist";
                break;
            case 141:
                returnMessage = "Unknown or invalid device";
                break;
            case 142:
                returnMessage = "Unsupported or invalid file type";
                break;
            case 143:
                returnMessage = "This file already exists";
                break;
            case 144:
                returnMessage = "This folder already exists";
                break;
            case 145:
                returnMessage = "The application trying to access the API is disabled";
                break;
            case 146:
                returnMessage = "The application trying to access the API is suspended";
                break;
            case 147:
                returnMessage = "Bulk downloading from multiple file owners is currently not supported";
                break;
            case 148:
                returnMessage = "Bulk download requires the file owner or the downloader to be premium";
                break;
            case 149:
                returnMessage = "The owner of the files is not a premium user. You need to confirm the download "
                        + "using your own bandwidth";
                break;
            case 150:
                returnMessage = "One or more files are too large to be included. Files must be "
                        + ZIP_MAX_FILESIZE_READABLE + " or less in order to be included in the "
                        + "zip file.";
                break;
            case 151:
                returnMessage = "The item you selected contained no files. You must select at least one file "
                        + "to zip";
                break;
            case 152:
                returnMessage = "None of the selected files were able to be zipped at this time";
                break;
            case 153:
                returnMessage = "The total size of the zip file is larger than " + ZIP_MAX_TOTAL_FILESIZE
                        + ". You need to confirm the download";
                break;
            case 154:
                returnMessage = "Maximum number of files reached. Cannot add more than " + ZIP_MAX_FILES
                        + " files";
                break;
            case 155:
                returnMessage = "The files owner does not have enough bandwidth to download the zip file. "
                        + "You need to confirm the download using your own bandwidth";
                break;
            case 156:
                returnMessage = "You do not have enough bandwidth to download the zip file.";
                break;
            case 157:
                returnMessage = "Neither the owner of the files nor you have enough bandwidth to download "
                        + "the zip file";
                break;
            case 158:
                returnMessage = "This file exists already";
                break;
            case 159:
                returnMessage = "This folder exists already";
                break;
            case 160:
                returnMessage = "The Terms of Service acceptance token is invalid";
                break;
            case 161:
                returnMessage = "You must accept the latest Terms of Service";
                break;
            case 162:
                returnMessage = "The file(s)/folder(s) you upload/copy exceed your total storage limit";
                break;
            case 163:
                returnMessage = "You have reached the limit accessing the API. Please try again later";
                break;
            case 164:
                returnMessage = "These files have already been reported";
                break;
            case 165:
                returnMessage = "These files no longer exist in our system";
                break;
            case 166:
                returnMessage = "Cannot add a private folder to an account";
                break;
            case 167:
                returnMessage = "Maximum depth of folder reached. Cannot add more than "
                        + FOLDER_DEPTH_LIMIT;
                break;
            case 168:
                returnMessage = "Invalid Product Id";
                break;
            case 169:
                returnMessage = "Upload Failed";
                break;
            case 170:
                returnMessage = "Can't change plan to one that is not in the same class with the current";
                break;
            case 171:
                returnMessage = "Can't change plan from/to business plan";
                break;
            case 172:
                returnMessage = "Can't change plan, plan will be expiring or it has already expired.";
                break;
            case 173:
                returnMessage = "Must be a premium user to use this function";
                break;
            case 174:
                returnMessage = "The URL specified is invalid";
                break;
            case 175:
                returnMessage = "The Upload Key specified is invalid";
                break;
            case 176:
                returnMessage = "The storage amount for this product is less than the total "
                        + "size of your files.";
                break;
            case 177:
                returnMessage = "Cannot insert a duplicate entry";
                break;
            case 178:
                returnMessage = "Cannot change to same plan";
                break;
            case 179:
                returnMessage = "Must change to a current product";
                break;
            case 180:
                returnMessage = "Cannot downgrade from a business account";
                break;
            case 181:
                returnMessage = "Error upgrading to business account";
                break;
            case 182:
                returnMessage = "You do not have enough credit to change to this plan. Please contact "
                        + "customer service.";
                break;
            case 183:
                returnMessage = "Changing to this product would give you negative bandwidth.";
                break;
            case 184:
                returnMessage = "The account you are trying to link is already linked to another "
                        + "MediaFire user.";
                break;
            case 185:
                returnMessage = "The specified Folder Name is invalid";
                break;
            case 186:
                returnMessage = "Cannot download password-protected files in bulk";
                break;
            case 187:
                returnMessage = "Found no server matching your request";
                break;
            case 188:
                returnMessage = "You must be logged in to purchase a plan";
                break;
            case 189:
                returnMessage = "You must agree to the reseller terms of service";
                break;
            case 190:
                returnMessage = "Business seats cannot make purchases";
                break;
            case 191:
                returnMessage = "This user is a banned buyer";
                break;
            case 192:
                returnMessage = "Error with reseller credits";
                break;
            case 193:
                returnMessage = "You may not purchase from this country";
                break;
            case 194:
                returnMessage = "The subdomain is in use or invalid";
                break;
            case 195:
                returnMessage = "This user has too many failed transactions";
                break;
            case 196:
                returnMessage = "The credit card you have entered is invalid";
                break;
            case 197:
                returnMessage = "You have purchased an account within the last 3 days";
                break;
            case 198:
                returnMessage = "There was an error storing the invoice";
                break;
            case 199:
                returnMessage = "A duplicate transaction has been submitted";
                break;
            case 200:
                returnMessage = "Invalid card CCV code";
                break;
            case 201:
                returnMessage = "This transaction has been declined";
                break;
            case 202:
                returnMessage = "Prepaid card error";
                break;
            case 203:
                returnMessage = "No response from MaxMind";
                break;
            case 204:
                returnMessage = "High risk rating from MaxMind";
                break;
            case 205:
                returnMessage = "Declined high risk rating from MaxMind";
                break;
            case 206:
                returnMessage = "There was an error storing the credit card";
                break;
            case 207:
                returnMessage = "Total number of files copied cannot exceed " + MAX_OBJECTS;
                break;
            case 208:
                returnMessage = "Another Asynchronous Operation is in progress. Please Retry later";
                break;
            case 209:
                returnMessage = "This folder has already been deleted";
                break;
            case 210:
                returnMessage = "This file has already been deleted";
                break;
            case 211:
                returnMessage = "Items in the Trash Can cannot be modified";
                break;
            case 212:
                returnMessage = "You cannot change from a free plan";
                break;
            case 213:
                returnMessage = "You cannot report these files";
                break;
            case 214:
                returnMessage = "The specified FileDrop Key is invalid";
                break;
            case 215:
                returnMessage = "The call signature is missing";
                break;
            case 216:
                returnMessage = "The email address provided must be greater than 3 characters";
                break;
            case 217:
                returnMessage = "The email address provided must be less than 50 characters";
                break;
            case 218:
                returnMessage = "Cannot register via Facebook. The Facebook Email is missing";
                break;
            case 219:
                returnMessage = "The Facebook Email is already registered with a MediaFire account";
                break;
            case 220:
                returnMessage = "Failed to authenticate to Facebook";
                break;
            case 221:
                returnMessage = "Failed to authenticate to Twitter";
                break;
            case 222:
                returnMessage = "The requested patch was not found or unknown";
                break;
            case 223:
                returnMessage = "The revision you requested is invalid or cannot be restored";
                break;
            case 224:
                returnMessage = "There is no active invoice to cancel";
                break;
            case 225:
                returnMessage = "This application is not allowed to log to the database";
                break;
            case 226:
                returnMessage = "Invalid installation ID";
                break;
            case 227:
                returnMessage = "The provided incident and installation ID's do not match";
                break;
            case 228:
                returnMessage = "The Facebook Access Token is required";
                break;
            case 229:
                returnMessage = "The Twitter OAuth Token is required";
                break;
            case 230:
                returnMessage = "This user has no associated avatar image";
                break;
            case 231:
                returnMessage = "The provided software token is invalid";
                break;
            case 232:
                returnMessage = "The email address of the sender is not yet validated";
                break;
            case 233:
                returnMessage = "Failed to authenticate to Google";
                break;
            case 234:
                returnMessage = "Failed to send message";
                break;
            case 235:
                returnMessage = "You own this resource";
                break;
            case 236:
                returnMessage = "You already follow this resource";
                break;
            case 237:
                returnMessage = "You do not have access to this resource";
                break;
            case 238:
                returnMessage = "This file has not changed; no need to update";
                break;
            case 239:
                returnMessage = "Maximum number of allowed share for this resource is reached";
                break;
            case 240:
                returnMessage = "Cannot grant permissions to the specified resource(s)";
                break;
            case 241:
                returnMessage = "The service number provided is not a recognized service";
                break;
            case 242:
                returnMessage = "The folder trying to be deleted has over 1000 files";
                break;
            case 243:
                returnMessage = "This account is temporarily locked. Please, try again later";
                break;
            case 244:
                returnMessage = "This service is available to US residents only";
                break;
            default:
                returnMessage = "Unknown response code";
        }
        return returnMessage;
    }
}
