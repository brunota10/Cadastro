package com.example.brunoalves.cadastro;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Salvar extends AppCompatActivity implements LocationListener {

    private String enderecoSalvar = "http://www.brunoasilva.com.br/android/salvarr.php?";
    private EditText cnpj1, fantasia1, logradouro1, numero1, bairro1, cep1, cidade1, telefone1, usuario1,senha1,repeteSenha1;
    private String cnpj, fantasia,logradouro,numero,bairro,cep,cidade,telefone,usuario,senha,repeteSenha,longitudee,latitudee,estado,retornoFinal;
    private Spinner estado1;
    private TextWatcher cnpjMask, telMask, cepMask;
    private String enderecoEstado = "http://www.brunoasilva.com.br/android/estado.php";
    private ProgressBar barra;
    private Spinner spinner;
    private Context contexto = this;
    private List<Estados> lista;
    protected LocationManager locationManager;
    Double latitude, longitude;
    private int qtdErros = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText cnpj = (EditText) findViewById(R.id.editTextCnpj);
        cnpjMask = Mask.insert("##.###.###/####-##", cnpj);
        cnpj.addTextChangedListener(cnpjMask);
        final EditText tel = (EditText) findViewById(R.id.editTextTelefone);
        telMask = Mask.insert("(##)#####-####", tel);
        tel.addTextChangedListener(telMask);
        final EditText cep = (EditText) findViewById(R.id.editTextCep);
        cepMask = Mask.insert("##.###-###", cep);
        cep.addTextChangedListener(cepMask);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e){
            Log.v("Erro Gps:", String.valueOf(e));
        }

        cnpj1 = ((EditText) findViewById(R.id.editTextCnpj));
        fantasia1 = ((EditText) findViewById(R.id.editTextFantasia));
        logradouro1 = ((EditText) findViewById(R.id.editTextLogradouro));
        numero1 = ((EditText) findViewById(R.id.editTextNumero));
        bairro1 = ((EditText) findViewById(R.id.editTextBairro));
        cep1 = ((EditText) findViewById(R.id.editTextCep));
        cidade1 = ((EditText) findViewById(R.id.editTextCidade));
        estado1 = (Spinner) findViewById(R.id.spinner);
        telefone1 = ((EditText) findViewById(R.id.editTextTelefone));
        usuario1 = ((EditText) findViewById(R.id.editTextUsuario));
        senha1 = ((EditText) findViewById(R.id.editTextSenha));
        repeteSenha1 = ((EditText) findViewById(R.id.editTextRepeteSenha));

        barra = (ProgressBar) findViewById(R.id.progressBar);
        spinner = (Spinner) findViewById(R.id.spinner);
        conexaoEstado(enderecoEstado);
    }

    //****************************************************************************************************************

    public String retiraAcentos(String input){
        return input.replaceAll("á|à|â|ã|ä", "a").replaceAll("Á|À|Ã|Â|Ä", "A").replaceAll("é|è|ê|ë", "e").replaceAll("É|È|Ê|Ë", "E")
                .replaceAll("í|ì|î|ï", "i").replaceAll("Í|Ì|Î|Ï", "I").replaceAll("ó|ò|õ|ô|ö", "o")
                .replaceAll("Ó|Ò|Õ|Ô|Ö", "O").replaceAll("ú|ù|û|ü", "u").replaceAll("Ú|Ù|Û|Ü", "U").replaceAll("ñ", "n")
                .replaceAll("Ñ", "N").replaceAll("ç", "c").replaceAll("Ç", "C");
    }

    //****************************************************************************************************************

    public String URLencode(String input) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, "UTF-8");
    }

    //****************************************************************************************************************

    public void validForm(View v) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        cnpj = cnpj1.getText().toString();  fantasia = fantasia1.getText().toString(); logradouro = logradouro1.getText().toString();
        numero = numero1.getText().toString();  bairro = bairro1.getText().toString();  cep = cep1.getText().toString();
        cidade = cidade1.getText().toString();  telefone = telefone1.getText().toString();  usuario = usuario1.getText().toString();
        senha = senha1.getText().toString();  repeteSenha = repeteSenha1.getText().toString();  estado = estado1.getSelectedItem().toString();
        latitudee = "10.100000"; longitudee = "10.100000";

        if(cnpj.equals("") || cnpj.length() < 18) {
            Toast.makeText(this, "Campo CNPJ deve ter 14 números!", Toast.LENGTH_SHORT).show();
            cnpj1.requestFocus();
            qtdErros ++;
        }
        if(fantasia.equals("") || cnpj.length() < 5){
            Toast.makeText(this, "Campo Nome Fantasia deve ter mais de 5 letras!", Toast.LENGTH_SHORT).show();
            fantasia1.requestFocus();
            qtdErros ++;
        }
        if(logradouro.equals("")){
            Toast.makeText(this, "Logradouro é obrigatório!", Toast.LENGTH_SHORT).show();
            logradouro1.requestFocus();
            qtdErros ++;
        }
        if(numero.equals("")){
            Toast.makeText(this, "Número é obrigatório!", Toast.LENGTH_SHORT).show();
            numero1.requestFocus();
            qtdErros ++;
        }
        if(bairro.equals("")){
            Toast.makeText(this, "Bairro é obrigatório!", Toast.LENGTH_SHORT).show();
            bairro1.requestFocus();
            qtdErros ++;
        }
        if(cep.equals("") || cep.length() < 10){
            Toast.makeText(this, "CEP deve ter 8 números!", Toast.LENGTH_SHORT).show();
            cep1.requestFocus();
            qtdErros ++;
        }
        if(cidade.equals("")){
            Toast.makeText(this, "Cidade é obrigatório!", Toast.LENGTH_SHORT).show();
            cidade1.requestFocus();
            qtdErros ++;
        }
        if(telefone.equals("") || telefone.length()  < 12){
            Toast.makeText(this, "Telefone informe DDD mais 9 digítos!!", Toast.LENGTH_SHORT).show();
            telefone1.requestFocus();
            qtdErros ++;
        }
        if(usuario.equals("") || usuario.length() < 5){
            Toast.makeText(this, "Usuário deve ter pelo menos 5 caracteres", Toast.LENGTH_SHORT).show();
            usuario1.requestFocus();
            qtdErros ++;
        }
        if(senha.equals("")) {
            Toast.makeText(this, "Senha é obrigatório!", Toast.LENGTH_SHORT).show();
            senha1.requestFocus();
            qtdErros ++;
        }
        if(repeteSenha.equals("")) {
            Toast.makeText(this, "Repetir Senha é obrigatório!", Toast.LENGTH_SHORT).show();
            repeteSenha1.requestFocus();
            qtdErros ++;
        }
        Log.v("Mensagem:", "QtdErros:"+qtdErros);
        if(qtdErros == 0){
            if(senha.equals(repeteSenha)){
                cnpj = URLencode(cnpj);
                numero = URLencode(numero);
                cep = URLencode(cep);
                telefone = URLencode(telefone);
                usuario = URLencode(usuario);
                senha = getBase64(senha);
                senha = URLencode(senha);
                estado = URLencode(estado);
                latitudee = URLencode(latitudee);
                longitudee = URLencode(longitudee);

                fantasia = retiraAcentos(fantasia);
                fantasia = fantasia.toUpperCase();
                fantasia = URLencode(fantasia);

                logradouro = retiraAcentos(logradouro);
                logradouro = logradouro.toUpperCase();
                logradouro = URLencode(logradouro);

                bairro = retiraAcentos(bairro);
                bairro = bairro.toUpperCase();
                bairro = URLencode(bairro);

                cidade = retiraAcentos(cidade);
                cidade = cidade.toUpperCase();
                cidade = URLencode(cidade);
                gravar(v);
            } else {
                Toast.makeText(this, "Senha não conferem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //****************************************************************************************************************

    public void gravar(View v) throws UnsupportedEncodingException {
        Log.v("Mensagem:", "Entrou no salvar");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo redeInfo = connMgr.getActiveNetworkInfo();
                if (redeInfo != null && redeInfo.isConnected()) {
                    String parte = "cnpj=" + cnpj + "&fantasia=" + fantasia + "&logradouro=" + logradouro +
                            "&numero=" + numero + "&bairro=" + bairro + "&cep=" + cep + "&cidade=" + cidade + "&estado=" + estado +
                            "&telefone=" + telefone + "&usuario=" + usuario + "&senha=" + senha + "&longitude=" + longitudee +
                            "&latitude=" + latitudee;
                    Log.v("angoti", parte);

                    String url = enderecoSalvar + parte;

                    Log.v("angoti", url);

                    new ComunicacaoAssincrona().execute(url);

                    cnpj1.setText("");fantasia1.setText("");logradouro1.setText("");numero1.setText("");bairro1.setText("");
                    cep1.setText("");cidade1.setText("");estado1.setSelection(0);telefone1.setText("");usuario1.setText("");
                    senha1.setText("");repeteSenha1.setText("");
                    cnpj1.requestFocus();
                } else {
                    Toast.makeText(this, R.string.msg2, Toast.LENGTH_SHORT).show();
                }
    }

    //****************************************************************************************************************

    public String getBase64(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
        byte messageDigest[] = algorithm.digest(input.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            hexString.append(String.format("%02X", 0xFF & b));
        }
        String senhahex = hexString.toString();

        return senhahex;
    }

    //****************************************************************************************************************

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //****************************************************************************************************************

    private void conexaoEstado(String url) {
        barra.setVisibility(View.VISIBLE);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo redeInfo = connMgr.getActiveNetworkInfo();
        if (redeInfo != null && redeInfo.isConnected()) {
            new ComunicacaoAssincronaEstados().execute(url);
        } else {
            Toast.makeText(this, R.string.msg2, Toast.LENGTH_SHORT).show();
        }
    }
    //****************************************************************************************************************

    private void novaTela() {
        Intent intent = new Intent();
        intent.setClass(Salvar.this,Cadastro.class);

        startActivity(intent);
    }

//****************************************************************************************************************

    private class ComunicacaoAssincrona extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return getString(R.string.msg3);
            }
        }

        @Override
        protected void onPostExecute(String resultado) {
            Toast.makeText(contexto, resultado, Toast.LENGTH_SHORT).show();
            //novaTela();
            finish();
        }
//
        private String downloadUrl(String myurl) throws IOException {
            InputStream retorno = null;
            try {
                URL url = new URL(myurl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000 /* milliseconds */);
                con.setConnectTimeout(15000 /* milliseconds */);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                // realiza a requisição HTPP
                con.connect();
                retorno = con.getInputStream();
                // Converte  a resposta em string
                String retornoString = converteStreamParaString(retorno);
                return retornoString;
            } finally {
                if (retorno != null) {
                    retorno.close();
                }
            }
        }

        // recebe um InputStream e retorna uma String
        public String converteStreamParaString(InputStream stream) throws IOException, UnsupportedEncodingException {
            byte[] buffer1k = new byte[1024];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int bytesLidos;
            while ((bytesLidos = stream.read(buffer1k)) != -1) {
                buffer.write(buffer1k, 0, bytesLidos);
            }
            return new String(buffer.toByteArray(), "UTF-8");
        }
    }

    //****************************************************************************************************************

    private class ComunicacaoAssincronaEstados extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return getString(R.string.msg3);
            }
        }

        @Override
        protected void onPostExecute(String resultado) {
            //Toast.makeText(contexto, resultado, Toast.LENGTH_SHORT).show();
            barra.setVisibility(View.GONE);
            ArrayAdapter<Estados> adaptador;
            try {
                JSONArray json = new JSONArray(resultado);
                lista = converteJSONemLista(json);
                adaptador = new ArrayAdapter<Estados>(contexto, android.R.layout.simple_list_item_1, lista);
                spinner.setAdapter(adaptador);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream retorno = null;
            try {
                URL url = new URL(myurl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000 /* milliseconds */);
                con.setConnectTimeout(15000 /* milliseconds */);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                // realiza a requisição HTPP
                con.connect();
                retorno = con.getInputStream();
                // Converte  a resposta em string
                String retornoString = converteStreamParaString(retorno);
                return retornoString;
            } finally {
                if (retorno != null) {
                    retorno.close();
                }
            }
        }

        // recebe um InputStream e retorna uma String
        public String converteStreamParaString(InputStream stream) throws IOException, UnsupportedEncodingException {
            byte[] buffer1k = new byte[1024];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int bytesLidos;
            while ((bytesLidos = stream.read(buffer1k)) != -1) {
                buffer.write(buffer1k, 0, bytesLidos);
            }
            return new String(buffer.toByteArray(), "UTF-8");
        }

        public List<Estados> converteJSONemLista(JSONArray json) throws JSONException {
            List<Estados> listaDeEstados = new ArrayList<Estados>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject u = json.getJSONObject(i);
                Estados estado = new Estados(
                        u.getInt("id"),
                        u.getString("nome"),
                        u.getString("uf"),
                        u.getInt("pais")
                );
                listaDeEstados.add(estado);
            }
            return listaDeEstados;
        }
    }
}
