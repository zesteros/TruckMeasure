package com.mx.vise.cubicaciones.settings;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mx.vise.cubicaciones.R;
import com.mx.vise.cubicaciones.activities.BuildingDataActivity;
import com.mx.vise.cubicaciones.dao.DAO;
import com.mx.vise.cubicaciones.dao.DatabaseHelper;
import com.mx.vise.cubicaciones.pojos.CubageProcessPOJO;
import com.mx.vise.cubicaciones.singleton.Singleton;
import com.mx.vise.cubicaciones.tasks.OnSyncListener;
import com.mx.vise.cubicaciones.tasks.SyncData;
import com.mx.vise.cubicaciones.tasks.SyncStatus;
import com.mx.vise.cubicaciones.util.TinyDB;
import com.mx.vise.updater.ApkDownloader;
import com.mx.vise.updater.AppUpdater;
import com.mx.vise.updater.enums.Display;
import com.mx.vise.updater.enums.UpdateFrom;

import net.sqlcipher.database.SQLiteDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static BluetoothAdapter mBTA;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        mBTA = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return
                PreferenceFragment.class.getName().equals(fragmentName) ||
                        DataSyncPreferenceFragment.class.getName().equals(fragmentName) ||
                        PrinterPreferenceFragment.class.getName().equals(fragmentName) ||
                        UpdatePreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        private static final float CUBAGE_UPLOAD_APROX_DURATION = 6.34f;
        private Preference mSyncNowPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);
            mSyncNowPreference = findPreference(getString(R.string.sync_now_key));

            mSyncNowPreference.setOnPreferenceClickListener(this);

            initSummary();
        }

        private void initSummary() {
            String lastUpdateDate = DAO.getLastUpdateDate(getActivity());
            if (lastUpdateDate != null) {
                mSyncNowPreference.setSummary("Ultima vez actualizado: " + lastUpdateDate);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceClick(final Preference preference) {
            int amountCubages = DAO.getAmountOfCubages(getActivity());
            if (amountCubages > 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                NumberFormat formatter = new DecimalFormat("#0.00");
                String duration = formatter.format(amountCubages * CUBAGE_UPLOAD_APROX_DURATION);
                builder.setMessage(getActivity().getString(R.string.sync_confirm).replace("%", duration))
                        .setTitle(getActivity().getString(R.string.pref_title_sync_now))
                        .setPositiveButton(R.string.go_ahead, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new SyncData(preference, getActivity(), Singleton.getInstance().getImei())
                                        .setOnSyncListener(new OnSyncListener() {
                                            @Override
                                            public void onSyncSuccessful(ArrayList<CubageProcessPOJO> cubagesFailed) {

                                                if (cubagesFailed.size() > 0) {

                                                    int sendCubages = amountCubages - cubagesFailed.size();

                                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                                    dialog
                                                            .setTitle("Cubicaciones enviadas")
                                                            .setMessage("Se han enviado " + sendCubages + " de " + amountCubages + " cubicaciones. Para enviar las pendientes inténtelo de nuevo.")
                                                            .setPositiveButton("Aceptar", null)
                                                            .show();
                                                }
                                            }

                                            @Override
                                            public void onSyncFailed(SyncStatus syncStatus, ArrayList<CubageProcessPOJO> cubagesFailed) {

                                            }
                                        })
                                        .execute();
                            }
                        })
                        .setNegativeButton(R.string.sync_later, null)
                        .show();
            } else {
                new SyncData(preference, getActivity(), Singleton.getInstance().getImei())
                        .setOnSyncListener(new OnSyncListener() {
                            @Override
                            public void onSyncSuccessful(ArrayList<CubageProcessPOJO> cubagesFailed) {
                                if (cubagesFailed.size() > 0) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                    dialog
                                            .setTitle("Cubicación no enviada")
                                            .setMessage("Ocurrió un error al enviar la cubicación, inténtelo nuevamente.")
                                            .setPositiveButton("Aceptar", null)
                                            .show();
                                }
                            }

                            @Override
                            public void onSyncFailed(SyncStatus syncStatus, ArrayList<CubageProcessPOJO> cubagesFailed) {

                            }
                        })
                        .execute();
            }

            return true;
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PrinterPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, DialogInterface.OnClickListener {

        private static final String TAG = "VISE";
        private Preference mEstablishPrinterPreference;
        private BroadcastReceiver mReceiver;
        private AlertDialog.Builder builderSingle;
        private ArrayAdapter<String> arrayAdapter;
        private ArrayList<BluetoothDevice> devices;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_bluetooth_printer);
            setHasOptionsMenu(true);
            devices = new ArrayList<>();
            mEstablishPrinterPreference = findPreference(getString(R.string.selected_printer_key));


            mEstablishPrinterPreference.setOnPreferenceClickListener(this);
            builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setTitle("Selecciona una impresora");

            arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.select_dialog_item);

            initSummary();

        }

        public void initSummary() {
            TinyDB tinyDB = new TinyDB(getActivity());
            String deviceName = tinyDB.getString(getActivity().getString(R.string.printer_name));
            if (deviceName != null) {
                mEstablishPrinterPreference.setSummary("Impresora establecida actualmente: " + deviceName);
            }
        }

        public void startDiscovery() {
            arrayAdapter.clear();
            devices.clear();
            builderSingle.setAdapter(arrayAdapter, PrinterPreferenceFragment.this);
            mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    //Finding devices
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        // Add the name and address to an array adapter to show in a ListView

                        if (!devices.contains(device)) {
                            devices.add(device);
                            if (device.getName() == null) {
                                arrayAdapter.add(device.getAddress());
                            } else
                                arrayAdapter.add(device.getName());

                            arrayAdapter.notifyDataSetChanged();
                            // Log.i(TAG, "onReceive: mac=" + device.getAddress() + ",name=" + device.getName());
                        }
                    }
                }
            };

            //check to see if there is BT on the Android device at all
            if (mBTA == null) {
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(getActivity(), "No Bluetooth on this handset", duration).show();
            }
            //let's make the user enable BT if it isn't already
            if (!mBTA.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, 0xDEADBEEF);
            }
            //cancel any prior BT device discovery
            if (mBTA.isDiscovering()) {
                mBTA.cancelDiscovery();
            }
            //re-start discovery

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mReceiver, filter);
            mBTA.startDiscovery();
            builderSingle.show();
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mBTA != null) {
                if (mBTA.isDiscovering())
                    mBTA.cancelDiscovery();

            }
            if (mReceiver != null)
                getActivity().unregisterReceiver(mReceiver);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            startDiscovery();
            return true;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, final int pos) {

            String deviceName = devices.get(pos).getName();

            final String macAddress = devices.get(pos).getAddress();

            deviceName = deviceName == null ? macAddress : deviceName;

            AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());

            final String finalDeviceName = deviceName;
            builderInner
                    .setMessage(deviceName)
                    .setTitle(R.string.printer_established)
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            TinyDB tinydb = new TinyDB(getActivity());
                            tinydb.putString(getActivity().getString(R.string.printer_name), finalDeviceName);
                            tinydb.putString(getActivity().getString(R.string.printer_mac), macAddress);
                            mEstablishPrinterPreference.setSummary(
                                    getActivity()
                                            .getString(R.string.printer_established_actually)
                                            .replace("%", finalDeviceName)
                            );
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UpdatePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        Preference mUpdateNowPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_update);
            setHasOptionsMenu(true);


            mUpdateNowPreference = findPreference(getString(R.string.update_now_key));

            mUpdateNowPreference.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            final AppUpdater appUpdater = new AppUpdater(getActivity());
            appUpdater
                    .setDisplay(Display.DIALOG)
                    .setTitleOnUpdateAvailable("Actualización disponible")
                    .setContentOnUpdateAvailable("Descarga la última versión")
                    .setTitleOnUpdateNotAvailable("No hay actualizaciones")
                    .setContentOnUpdateNotAvailable("No hay actualizaciones disponibles, verifica luego.")
                    .setButtonUpdate("Actualizar ahora")
                    .setButtonDismiss("Cancelar")
                    .setUpdateFrom(UpdateFrom.XML)
                    .setUpdateXML("https://linux.vise.com.mx/descargas/cubicaciones/update_cubicaciones.xml")
                    .setButtonUpdateClickListener((dialog, which) -> {

                        boolean cubagesToSend = DAO.thereIsCubagesToSend(getActivity());
                        if (cubagesToSend) {
                            AlertDialog.Builder uploadDataFirstDialog = new AlertDialog.Builder(getActivity());
                            uploadDataFirstDialog
                                    .setMessage("Asegúrese de haber subido los datos pendientes, ya que después de actualizar se eliminarán TODOS los datos. ¿Deseas continuar?, se perderán los datos no sincronizados.")
                                    .setTitle("Detección de datos no enviados")
                                    .setPositiveButton("Continuar", (dialog1, which1) -> {
                                        showDownloadApkDialog(appUpdater);
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        } else {
                            showDownloadApkDialog(appUpdater);
                        }
                    })
                    .setIcon(R.drawable.ic_stat_name)
                    .setButtonDismiss("Actualizar después");

            appUpdater.start();
            return false;
        }

        public void showDownloadApkDialog(AppUpdater appUpdater) {
            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());

            SQLiteDatabase db = databaseHelper.getWritableDatabase(DatabaseHelper.DATABASE_K);

            databaseHelper.onUpgradeAll(db, 0, 0);

            String url = appUpdater.getUpdate().getUrlToDownload().toString();
            new ApkDownloader(getActivity()).showDownloadDialog(url);
            appUpdater.dismiss();
        }

    }


}
