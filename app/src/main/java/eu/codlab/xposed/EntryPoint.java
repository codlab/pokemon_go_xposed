package eu.codlab.xposed;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import eu.codlab.MapObject;
import eu.codlab.PublicProto;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by kevinleperf on 09/07/2016.
 */

public class EntryPoint implements IXposedHookLoadPackage {

    private Handler _handler;
    private Activity mActivity;

    private void outputLog(final Context context, final String string) {
        if (_handler == null) {
            _handler = new Handler(Looper.getMainLooper());
        }

        _handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, string, Toast.LENGTH_LONG).show();
            }
        });
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("\\x%02x", b);
        }

        return sb.toString();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.nianticlabs.pokemongo"))
            return;

        Log.d("PokemonGO", "we are in the Pokemon GO");

        final Class activity = loadPackageParam.classLoader.loadClass("com.unity3d.player.UnityPlayerNativeActivity");
        final Class Status = loadPackageParam.classLoader.loadClass("com.nianticlabs.nia.account.NianticAccountManager$Status");
        final Class NianticAccountManager = loadPackageParam.classLoader.loadClass("com.nianticlabs.nia.account.NianticAccountManager");
        final Class NiaNet = loadPackageParam.classLoader.loadClass("com.nianticlabs.nia.network.NiaNet");

        final ManageTopActivity activity_manager = new ManageTopActivity();

        Method[] methods = NianticAccountManager.getMethods();
        Method[] methods_nianet = NiaNet.getMethods();
        Method[] methods_activity = activity.getMethods();

        for (Method method : methods_activity) {
            Log.d("PokemonGO", "UnityPlayerNativeActivity : " + method.getName());
        }
        for (Method method : methods) {
            Log.d("PokemonGO", "NianticAccountManager : " + method.getName());
        }

        for (Method method : methods_nianet) {
            Log.d("PokemonGO", "NiaNet : " + method.getName());
        }

        try {
            findAndHookMethod(activity, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Activity activity = (Activity) param.thisObject;
                        mActivity = activity;
                        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("PokemonGO", "afterHookedMethod");

                    activity_manager.setActivity((Activity) param.thisObject);
                    activity_manager.onCreate();
                    super.afterHookedMethod(param);
                }
            });

            findAndHookMethod(activity, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    activity_manager.onResume();
                    super.afterHookedMethod(param);
                }
            });

            findAndHookMethod(activity, "onPause", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    activity_manager.onPause();
                    super.beforeHookedMethod(param);
                }
            });
        } catch (Exception e) {

        }

        try {
            findAndHookMethod(NianticAccountManager, "setAuthToken", Status, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object[] args = param.args;
                    Log.d("PokemonGO", "setAuthToken " + Arrays.toString(args));

                    super.beforeHookedMethod(param);
                }
            });

            findAndHookMethod(NiaNet, "request", long.class, int.class, String.class, int.class, String.class, ByteBuffer.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object[] args = param.args;
                    Log.d("PokemonGO", "request params /// " + Arrays.toString(args));

                    super.beforeHookedMethod(param);
                }
            });

            findAndHookMethod(NiaNet, "nativeCallback", long.class, int.class, String.class, ByteBuffer.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object[] args = param.args;
                    Log.d("PokemonGO", "nativeCallback " + Arrays.toString(args));

                    super.beforeHookedMethod(param);
                }
            });

            findAndHookMethod(NiaNet, "readDataSteam", HttpURLConnection.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Field bufferget = NiaNet.getDeclaredField("readBuffer");
                    bufferget.setAccessible(true);

                    ThreadLocal<ByteBuffer> buffer = (ThreadLocal<ByteBuffer>) bufferget.get(null);
                    Log.d("PokemonGO", "readDataStream value read := " + buffer);

                    if (buffer != null) {
                        ByteBuffer buff = buffer.get();
                        int offset = buff.arrayOffset();
                        int length = (int) param.getResult();

                        byte[] array = buff.array();
                        byte[] bytes = new byte[length];
                        int i = 0;
                        int j = offset;

                        if (length >= 0) {
                            for (; i < length; i++, j++) {
                                bytes[i] = array[j];
                            }
                        }

                        String res = bytesToHex(bytes);

                        Log.d("PokemonGO", "printf \"" + res + "\" > file");
                        Log.d("PokemonGO", "array? bytes 1 := " + length + " " + Arrays.toString(bytes));

                        try {
                            MapObject.MapObjectsResponse env = MapObject.MapObjectsResponse.parseFrom(bytes);
                            if (env != null) {

                                String output = "";
                                List<MapObject.MapObjectsResponse.Payload> payloads = env.getPayloadList();
                                for (MapObject.MapObjectsResponse.Payload payload : payloads) {
                                    for (MapObject.MapObjectsResponse.Payload.ClientMapCell map : payload.getProfileList()) {
                                        for (MapObject.MapObjectsResponse.Payload.WildPokemonProto pokemon : map.getWildPokemonList()) {
                                            double latitude = pokemon.getLatitude();
                                            double longitude = pokemon.getLongitude();
                                            MapObject.MapObjectsResponse.Payload.WildPokemonProto.Pokemon mon = pokemon.getPokemon();
                                            int id = mon.getPokemonId();

                                            output += "MAPPROTO having pokemon " + id + " " + latitude + " " + longitude + "\n";
                                        }

                                        for (MapObject.MapObjectsResponse.Payload.MapPokemonProto maproto : map.getMapPokemonList()) {
                                            output += "MAPPROTO map proto " + maproto.getPokedexTypeId().getNumber() + " " + maproto.getLatitude() + " " + maproto.getLongitude() + "\n";
                                        }

                                        for (MapObject.MapObjectsResponse.Payload.ClientSpawnPointProto spawn : map.getSpawnPointList()) {
                                            output += "MAPPROTO spawn point " + spawn.getLatitude() + " " + spawn.getLongitude() + "\n";
                                        }

                                        for (MapObject.MapObjectsResponse.Payload.NearbyPokemonProto nearby : map.getNearbyPokemonList()) {
                                            output += "MAPPROTO pokemon " + nearby.getPokedexNumber() + " " + nearby.getDistanceMeters() + "\n";
                                        }
                                    }
                                }

                                if (mActivity != null) outputLog(mActivity, output);
                            }
                        } catch (Exception e) {

                        }
                        try {
                            PublicProto.ResponseEnvelop env = PublicProto.ResponseEnvelop.parseFrom(bytes);

                            PublicProto.ResponseEnvelop.Unknown7 unk7 = env.getUnknown7();
                            if (unk7 != null) {
                                Log.d("PokemonGO", "unknown 7 bytes 1 " + bytesToHex(unk7.getUnknown71().toByteArray()));
                                Log.d("PokemonGO", "unknown 7 bytes 2 " + unk7.getUnknown72());
                                Log.d("PokemonGO", "unknown 7 bytes 3 " + bytesToHex(unk7.getUnknown73().toByteArray()));
                            }
                        } catch (Exception e) {
                            Log.d("PokemonGO", "having issue with current element");
                            e.printStackTrace();
                        }
                    }

                    super.afterHookedMethod(param);
                }
            });

            findAndHookMethod(NiaNet, "getMethodString", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object[] args = param.args;
                    Log.d("PokemonGO", "getMethodString " + Arrays.toString(args));

                    super.beforeHookedMethod(param);
                }
            });

            findAndHookMethod(NiaNet, "setHeaders", HttpURLConnection.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object[] args = param.args;
                    Log.d("PokemonGO", "setHeaders " + Arrays.toString(args) + " result := " + param.getResult());

                    super.beforeHookedMethod(param);
                }
            });

            findAndHookMethod(NiaNet, "joinHeaders", HttpURLConnection.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object[] args = param.args;
                    Log.d("PokemonGO", "joinHeaders " + Arrays.toString(args) + " result := " + param.getResult());

                    super.beforeHookedMethod(param);
                }
            });
        } catch (Exception e) {
            Log.d("PokemonGO", e.getMessage());
            e.printStackTrace();
        }
    }
}
