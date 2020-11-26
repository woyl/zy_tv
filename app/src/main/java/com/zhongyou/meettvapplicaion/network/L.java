package com.zhongyou.meettvapplicaion.network;

/**
 * Log统一管理类
 */
public class L {
    /**
     * 不要让任何人实例化这个类。
     */
    private L() {
        throw new Error("Do not need instantiate!");
    }

    /**
     * 主开关。捕捉错误信息你需要设置这个值低于log.warn
     */
    public static final int DEBUG_LEVEL = 0;

    /**
     * 系统L开关。当它是真的时，你可以看到LOG。
     * 否则，你不能。
     */
    public static final boolean DEBUG_SYSOUT = true;

    /**
     * Send a {@link android.util.Log#VERBOSE} log message.
     *
     * @param obj
     */
    public static void v(Object obj) {
        if (android.util.Log.VERBOSE > DEBUG_LEVEL) {
            String tag = getClassName();
            String msg = obj != null ? obj.toString() : "obj == null";
            android.util.Log.v(tag, msg);
        }
    }

    /**
     * Send a {@link #DEBUG_LEVEL} log message.
     *
     * @param obj
     */
    public static void d(Object obj) {
        if (android.util.Log.DEBUG > DEBUG_LEVEL) {
            String tag = getClassName();
            String msg = obj != null ? obj.toString() : "obj == null";
            android.util.Log.d(tag, msg);
        }
    }

    /**
     * Send an {@link android.util.Log#INFO} log message.
     *
     * @param obj
     */
    public static void i(Object obj) {
        if (android.util.Log.INFO > DEBUG_LEVEL) {
            String tag = getClassName();
            String msg = obj != null ? obj.toString() : "obj == null";
            android.util.Log.i(tag, msg);
        }
    }

    /**
     * Send a {@link android.util.Log#WARN} log message.
     *
     * @param obj
     */
    public static void w(Object obj) {
        if (android.util.Log.WARN > DEBUG_LEVEL) {
            String tag = getClassName();
            String msg = obj != null ? obj.toString() : "obj == null";
            android.util.Log.w(tag, msg);
        }
    }

    /**
     * Send an {@link android.util.Log#ERROR} log message.
     *
     * @param obj
     */
    public static void e(Object obj) {
        if (android.util.Log.ERROR > DEBUG_LEVEL) {
            String tag = getClassName();
            String msg = obj != null ? obj.toString() : "obj == null";
            android.util.Log.e(tag, msg);
        }
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen. The
     * error will always be logged at level ASSERT with the call stack.
     * Depending on system configuration, a report may be added to the
     * {@link android.os.DropBoxManager} and/or the process may be terminated
     * immediately with an error dialog.
     *
     * @param obj
     */
    public static void wtf(Object obj) {
        if (android.util.Log.ASSERT > DEBUG_LEVEL) {
            String tag = getClassName();
            String msg = obj != null ? obj.toString() : "obj == null";
            android.util.Log.wtf(tag, msg);
        }
    }

    /**
     * Send a {@link android.util.Log#VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if (android.util.Log.VERBOSE > DEBUG_LEVEL) {
            android.util.Log.v(tag, msg);
        }
    }

    /**
     * Send a {@link #DEBUG_LEVEL} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        if (android.util.Log.DEBUG > DEBUG_LEVEL) {
            android.util.Log.d(tag, msg);
        }
    }

    /**
     * Send an {@link android.util.Log#INFO} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (android.util.Log.INFO > DEBUG_LEVEL) {
            android.util.Log.i(tag, msg);
        }
    }

    /**
     * Send a {@link android.util.Log#WARN} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        if (android.util.Log.WARN > DEBUG_LEVEL) {
            android.util.Log.w(tag, msg);
        }
    }

    /**
     * Send an {@link android.util.Log#ERROR} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        if (android.util.Log.ERROR > DEBUG_LEVEL) {
            android.util.Log.e(tag, msg);
        }
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen. The
     * error will always be logged at level ASSERT with the call stack.
     * Depending on system configuration, a report may be added to the
     * {@link android.os.DropBoxManager} and/or the process may be terminated
     * immediately with an error dialog.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    public static void wtf(String tag, String msg) {
        if (android.util.Log.ASSERT > DEBUG_LEVEL) {
            android.util.Log.wtf(tag, msg);
        }
    }

    /**
     * Send a {@link android.util.Log#VERBOSE} log message. And just print method name and
     * position in black.
     */
    public static void print() {
        if (android.util.Log.VERBOSE > DEBUG_LEVEL) {
            String tag = getClassName();
            String method = callMethodAndLine();
            android.util.Log.v(tag, method);
            if (DEBUG_SYSOUT) {
                System.out.println(tag + "  " + method);
            }
        }
    }

    /**
     * Send a {@link #DEBUG_LEVEL} log message.
     *
     * @param object The object to print.
     */
    public static void print(Object object) {
        if (android.util.Log.DEBUG > DEBUG_LEVEL) {
            String tag = getClassName();
            String method = callMethodAndLine();
            String content = "";
            if (object != null) {
                content = object.toString() + "                    ----    "
                        + method;
            } else {
                content = " ## " + "                ----    " + method;
            }
            android.util.Log.d(tag, content);
            if (DEBUG_SYSOUT) {
                System.out.println(tag + "  " + content + "  " + method);
            }
        }
    }

    /**
     * Send an {@link android.util.Log#ERROR} log message.
     *
     * @param object The object to print.
     */
    public static void printError(Object object) {
        if (android.util.Log.ERROR > DEBUG_LEVEL) {
            String tag = getClassName();
            String method = callMethodAndLine();
            String content = "";
            if (object != null) {
                content = object.toString() + "                    ----    "
                        + method;
            } else {
                content = " ## " + "                    ----    " + method;
            }
            android.util.Log.e(tag, content);
            if (DEBUG_SYSOUT) {
                System.err.println(tag + "  " + method + "  " + content);
            }
        }
    }

    /**
     * Print the array of stack trace elements of this method in black.
     *
     * @return
     */
    public static void printCallHierarchy() {
        if (android.util.Log.VERBOSE > DEBUG_LEVEL) {
            String tag = getClassName();
            String method = callMethodAndLine();
            String hierarchy = getCallHierarchy();
            android.util.Log.v(tag, method + hierarchy);
            if (DEBUG_SYSOUT) {
                System.out.println(tag + "  " + method + hierarchy);
            }
        }
    }

    /**
     * Print debug log in blue.
     *
     * @param object The object to print.
     */
    public static void printMyLog(Object object) {
        if (android.util.Log.DEBUG > DEBUG_LEVEL) {
            String tag = "MYLOG";
            String method = callMethodAndLine();
            String content = "";
            if (object != null) {
                content = object.toString() + "                    ----    "
                        + method;
            } else {
                content = " ## " + "                ----    " + method;
            }
            android.util.Log.d(tag, content);
            if (DEBUG_SYSOUT) {
                System.out.println(tag + "  " + content + "  " + method);
            }
        }
    }

    private static String getCallHierarchy() {
        String result = "";
        StackTraceElement[] trace = (new Exception()).getStackTrace();
        for (int i = 2; i < trace.length; i++) {
            result += "\r\t" + trace[i].getClassName() + ""
                    + trace[i].getMethodName() + "():"
                    + trace[i].getLineNumber();
        }
        return result;
    }

    private static String getClassName() {
        String result = "";
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        result = thisMethodStack.getClassName();
        return result;
    }

    /**
     * Realization of double click jump events.
     *
     * @return
     */
    private static String callMethodAndLine() {
        String result = "at ";
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        result += thisMethodStack.getClassName() + "";
        result += thisMethodStack.getMethodName();
        result += "(" + thisMethodStack.getFileName();
        result += ":" + thisMethodStack.getLineNumber() + ")  ";
        return result;
    }

}
