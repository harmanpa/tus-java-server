package me.desair.tus.server;

/**
 * Class that will hold constants for all HTTP headers relevant to the tus v1.0.0 protocol
 */
public class HttpHeader {
    /**
     * The X-HTTP-Method-Override request header MUST be a string which MUST be interpreted as the request’s method
     * by the Server, if the header is presented. The actual method of the request MUST be ignored.
     * The Client SHOULD use this header if its environment does not support the PATCH or DELETE methods.
     */
    public static final String METHOD_OVERRIDE = "X-HTTP-Method-Override";

    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * The Upload-Offset request and response header indicates a byte offset within a resource.
     * The value MUST be a non-negative integer.
     */
    public static final String UPLOAD_OFFSET = "Upload-Offset";
    public static final String UPLOAD_METADATA = "Upload-Metadata";

    /**
     * The Upload-Length request and response header indicates the size of the entire upload in bytes.
     * The value MUST be a non-negative integer.
     */
    public static final String UPLOAD_LENGTH = "Upload-Length";

    /**
     * The Tus-Version response header MUST be a comma-separated list of protocol versions supported by the Server.
     * The list MUST be sorted by Server’s preference where the first one is the most preferred one.
     */
    public static final String TUS_VERSION = "Tus-Version";

    /**
     * The Tus-Resumable header MUST be included in every request and response except for OPTIONS requests.
     * The value MUST be the version of the protocol used by the Client or the Server.
     */
    public static final String TUS_RESUMABLE = "Tus-Resumable";

    /**
     * The Tus-Extension response header MUST be a comma-separated list of the extensions supported by the Server.
     * If no extensions are supported, the Tus-Extension header MUST be omitted.
     */
    public static final String TUS_EXTENSION = "Tus-Resumable";

    /**
     * The Tus-Max-Size response header MUST be a non-negative integer indicating the maximum allowed size of an
     * entire upload in bytes. The Server SHOULD set this header if there is a known hard limit.
     */
    public static final String TUS_MAX_SIZE = "Tus-Max-Size";

}