package ru.obsession.iremote;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ServerApi {

    private interface TimeLoaded
    {
        void OnLoad(String time);
    }

    public static final String LOGIN_URL = "http://iremote-unit.tk/api/";
    private static final String MD5_KEY = "secret_key_here";
    private static volatile ServerApi serverApi;

    private RequestQueue queue;

    private ServerApi(RequestQueue queue) {
        this.queue = queue;
    }

    public static ServerApi getInstance(Context context) {
        if (serverApi == null) {
            synchronized (ServerApi.class) {
                if (serverApi == null) {
                    serverApi = new ServerApi(Volley.newRequestQueue(context));
                }
            }
        }
        return serverApi;
    }


    private void getTime(final TimeLoaded tl, Response.ErrorListener errorListener)
    {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                (Response.Listener<String>) new Response.Listener<String>() {
                    @Override
                    public void onResponse(String time) {
                        tl.OnLoad(time);
                    }
                }, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<>();
                mapObject.put("type", "get_server_time");
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void getBrands( final Response.Listener<String> responseListener, final Response.ErrorListener errorListener) {
        getTime(new TimeLoaded() {
            @Override
            public void OnLoad(final String time) {
                StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                        responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> mapObject = new HashMap<>();
                        mapObject.put("type", "get_brands");
                        mapObject.put("time", time);
                        mapObject.put("hash", md5(MD5_KEY + time + "get_brands"));
                        return mapObject;
                    }
                };

                queue.add(getRequest);
            }
        },errorListener);

    }

    public void getDevicesByBrand(final String brand, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener) {
        getTime(new TimeLoaded() {
            @Override
            public void OnLoad(final String time) {
                StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                        responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> mapObject = new HashMap<>();
                        mapObject.put("type", "get_devices_by_brand");
                        mapObject.put("brand", brand);
                        mapObject.put("time", time);
                        mapObject.put("hash", md5(MD5_KEY + time + "get_devices_by_brand" + brand));
                        return mapObject;
                    }
                };
                queue.add(getRequest);
            }
        }, errorListener);

    }

    public void getDevice(final String brand, final String device,
                                final Response.Listener<String> responseListener, final Response.ErrorListener errorListener) {
        getTime(new TimeLoaded() {
            @Override
            public void OnLoad(final String time) {
                StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                        responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> mapObject = new HashMap<>();
                        mapObject.put("type", "get_device_data");
                        mapObject.put("brand", brand);
                        mapObject.put("device", device);
                        mapObject.put("time", time);
                        mapObject.put("hash", md5(MD5_KEY + time + "get_device_data" + brand + device));
                        return mapObject;
                    }
                };
                queue.add(getRequest);
            }
        }, errorListener);
    }

    public void fingDivece(final int shift, final String pattert,
                           final Response.Listener<String> responseListener,
                           final Response.ErrorListener errorListener) {
        getTime(new TimeLoaded() {
            @Override
            public void OnLoad(final String time) {
                StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                        responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> mapObject = new HashMap<>();
                        mapObject.put("type", "search_devices");
                        mapObject.put("pattern", pattert);
                        mapObject.put("shift", String.valueOf(shift));
                        mapObject.put("time", time);
                        mapObject.put("hash", md5(MD5_KEY + time + "search_devices" + pattert + shift));
                        return mapObject;
                    }
                };
                queue.add(getRequest);
            }
        }, errorListener);

    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
