package com.example.regorodri.claustroinetandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class Claustros extends Activity {

    private String urlServidor = "http://regorodri.noip.me/proyecto/librerias/php/android.php";
    private ListView listView;
    private String[] clientes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clautros);
        listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int posicion,
                                    long arg3) {
                // TODO Auto-generated method stub
                String value = (String) arg0.getItemAtPosition(posicion);
                Log.d("datos", value);
                if (value.equals("No hay claustro para hoy")) {
                    Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ha pulsado el elemento " + posicion + " jaja saludos " + value, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), pintando.class);
                    i.putExtra("nombre", value);
                    //startActivityForResult(i, 1);
                    startActivity(i);
                }
            }

        });
        ReadJSONTask jsonTask = new ReadJSONTask();
        jsonTask.execute(urlServidor);
    }
    /*
    * Método para Pasar InputSream a String
    */

    private String readStream(InputStream in) {
        try {
            BufferedReader r = null;
            r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            if (r != null) {
                r.close();
            }
            in.close();
            return total.toString();
        } catch (IOException e) {
            return "Problemas leyendo de servidor " + e.toString();
        }
    }

    /*
    * AsyncTask para conectarse a mysql y descargar los datos Json.
    */
    public class ReadJSONTask extends AsyncTask<String, Void, ArrayAdapter<String>> {
        private ProgressDialog dialog = new ProgressDialog(com.example.regorodri.claustroinetandroid.Claustros.this);

        @Override
        protected ArrayAdapter<String> doInBackground(String... urls) {
            //mostrar cargando

            String body = " ";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String codigoRespuesta = Integer.toString(urlConnection.getResponseCode());
                if (codigoRespuesta.equals("200")) {
                    //Vemos si es 200 OK y leemos el cuerpo del mensaje.
                    body = readStream(urlConnection.getInputStream());
                    JSONArray jsonArray = new JSONArray(body);
                    //inicializamos el array con la longitud de la respuesta
                    clientes = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String nombre = jsonObject.getString("nombre");
                        clientes[i] = nombre;
                    }
                }
                Log.d("JSON", "JSON Todo= " + body);

                urlConnection.disconnect();
            } catch (JSONException e) {
                Log.e("ErrorJSON", "Error =  " + e.toString());

            } catch (MalformedURLException e) {
                Log.e("ErrorURL", "Error URL incorrecta: " + e.toString());

            } catch (SocketTimeoutException e) {
                Log.e("ErrorURL", "Error Finalizado el timeout esperando la respuesta del servidor: " + e.toString());

            } catch (IOException e) {
                Log.e("Error InputStream", "Error: " + e.toString());

            } catch (Exception e) {
                Log.e("Error", "Error: " + e.toString());

            }
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(com.example.regorodri.claustroinetandroid.Claustros.this,
                    android.R.layout.simple_list_item_1, clientes);
            return adaptador;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Cargando...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            super.onPostExecute(result);
            //paramos mensaje
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Descargado de la url", Toast.LENGTH_SHORT).show();

            //Rellenamos la lista con los resultados
            listView.setAdapter(result);
            // listView.setOnClickListener(this);

        }
    }
}

