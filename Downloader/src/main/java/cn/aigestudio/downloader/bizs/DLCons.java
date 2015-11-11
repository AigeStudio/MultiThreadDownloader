package cn.aigestudio.downloader.bizs;

final class DLCons {
    private DLCons() {
    }

    static final class Base {
        static final int DEFAULT_TIMEOUT = 20000;
        static final int MAX_REDIRECTS = 5;
        static final int LENGTH_PER_THREAD = 10485760;
    }

    static final class Code {
        static final int HTTP_CONTINUE = 100;
        static final int HTTP_SWITCHING_PROTOCOLS = 101;
        static final int HTTP_PROCESSING = 102;

        static final int HTTP_OK = 200;
        static final int HTTP_CREATED = 201;
        static final int HTTP_ACCEPTED = 202;
        static final int HTTP_NOT_AUTHORITATIVE = 203;
        static final int HTTP_NO_CONTENT = 204;
        static final int HTTP_RESET = 205;
        static final int HTTP_PARTIAL = 206;
        static final int HTTP_MULTI_STATUS = 207;

        static final int HTTP_MULT_CHOICE = 300;
        static final int HTTP_MOVED_PERM = 301;
        static final int HTTP_MOVED_TEMP = 302;
        static final int HTTP_SEE_OTHER = 303;
        static final int HTTP_NOT_MODIFIED = 304;
        static final int HTTP_USE_PROXY = 305;
        static final int HTTP_TEMP_REDIRECT = 307;

        static final int HTTP_BAD_REQUEST = 400;
        static final int HTTP_UNAUTHORIZED = 401;
        static final int HTTP_PAYMENT_REQUIRED = 402;
        static final int HTTP_FORBIDDEN = 403;
        static final int HTTP_NOT_FOUND = 404;
        static final int HTTP_BAD_METHOD = 405;
        static final int HTTP_NOT_ACCEPTABLE = 406;
        static final int HTTP_PROXY_AUTH = 407;
        static final int HTTP_CLIENT_TIMEOUT = 408;
        static final int HTTP_CONFLICT = 409;
        static final int HTTP_GONE = 410;
        static final int HTTP_LENGTH_REQUIRED = 411;
        static final int HTTP_PRECON_FAILED = 412;
        static final int HTTP_ENTITY_TOO_LARGE = 413;
        static final int HTTP_REQ_TOO_LONG = 414;
        static final int HTTP_UNSUPPORTED_TYPE = 415;
        static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
        static final int HTTP_EXPECTATION_FAILED = 417;
        static final int HTTP_UNPROCESSABLE_ENTITY = 422;
        static final int HTTP_LOCKED = 423;
        static final int HTTP_FAILED_DEPENDENCY = 424;

        static final int HTTP_INTERNAL_ERROR = 500;
        static final int HTTP_NOT_IMPLEMENTED = 501;
        static final int HTTP_BAD_GATEWAY = 502;
        static final int HTTP_UNAVAILABLE = 503;
        static final int HTTP_GATEWAY_TIMEOUT = 504;
        static final int HTTP_VERSION = 505;
        static final int HTTP_INSUFFICIENT_STORAGE = 507;
    }
}