package com.example.soundtouch.common;

import android.os.Message;

public class AsyncResult
{

    /*************************** Instance Variables **************************/

    // Expect either exception or result to be null

    public Object userObj;

    public Throwable exception;

    public Object result;

    /***************************** Class Methods *****************************/

    /** Saves and sets m.obj */
    public static AsyncResult
    forMessage(Message m, Object r, Throwable ex)
    {
        AsyncResult ret;

        ret = new AsyncResult (m.obj, r, ex);

        m.obj = ret;

        return ret;
    }

    /** Saves and sets m.obj */
    public static AsyncResult
    forMessage(Message m)
    {
        AsyncResult ret;

        ret = new AsyncResult (m.obj, null, null);

        m.obj = ret;

        return ret;
    }

    /** please note, this sets m.obj to be this */
    public
    AsyncResult (Object uo, Object r, Throwable ex)
    {
        userObj = uo;
        result = r;
        exception = ex;
    }
}
