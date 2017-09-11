package com.example.tux.cambiogrises;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private final int SELECT_PICTURE= 200;
    Button btnSelectImage, btnTrasnformImage;
    ImageView ivOriginal,ivGray;
    private Bitmap imgOriginal=null, imgTransformada=null;
    TextView txtHistograma;

    ProgressBar barProgreso;
    TextView txtPorcentaje;
    int width,height;
    boolean bandera = false;
    Bitmap.Config config = null;
    EditText etPotencia;

    SeekBar sbUmbral1,sbUmbral2;
    TextView tvUmbral1,tvUmbral2, tvUmbral1Color,tvUmbral2Color;

    //Variables para la grafica
    protected BarChart mChart;
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<IBarDataSet> dataSets = new ArrayList<>();

    BarDataSet barDataSet;


    int Histograma[]= new int[256];
    String opcion = "gris";
    double umbral1=0,umbral2=0,potencia=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChart = (BarChart) findViewById(R.id.chart1);

        /*
        dates= new ArrayList<>();
        dates.add("Enero");
        dates.add("Febrero");
        dates.add("Marzo");
        dates.add("Abril");

        barEntries.add(new BarEntry(1,2));
        barEntries.add(new BarEntry(2,2));
        barEntries.add(new BarEntry(3,2));
        barEntries.add(new BarEntry(4,2));

        barDataSet = new BarDataSet(barEntries,"Dates");
        dataSets.add((IBarDataSet) barDataSet);

        BarData data = new BarData(barDataSet);
        mChart.setData(data);*/

        //X-axis
        /*XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(6);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));*/


        //Y-axis
        /*mChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);*/


        ivOriginal = (ImageView)  findViewById(R.id.ivOriginal);
        ivGray = (ImageView)  findViewById(R.id.ivGray);

        btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        btnTrasnformImage = (Button) findViewById(R.id.btnTrasnformImage);


        txtPorcentaje = (TextView) findViewById(R.id.txtPorcentaje);
        barProgreso = (ProgressBar) findViewById(R.id.barProgreso);

        txtHistograma = (TextView) findViewById(R.id.txtHistograma);


        etPotencia = (EditText) findViewById(R.id.etPotencia);

        sbUmbral1 = (SeekBar) findViewById(R.id.sbUmbral1);
        sbUmbral1.setMax(255);
        tvUmbral1 = (TextView)findViewById(R.id.tvUmbral1);
        tvUmbral1Color = (TextView)findViewById(R.id.tvUmbral1Color);
        tvUmbral1Color.setBackgroundColor(Color.rgb(0,0,0));

        sbUmbral2 = (SeekBar) findViewById(R.id.sbUmbral2);
        sbUmbral2.setMax(255);
        tvUmbral2 = (TextView)findViewById(R.id.tvUmbral2);
        tvUmbral2Color = (TextView)findViewById(R.id.tvUmbral2Color);
        tvUmbral2Color.setBackgroundColor(Color.rgb(1,1,1));

        sbUmbral2.setProgress(1);
        tvUmbral2.setText(""+1);
        btnTrasnformImage.setEnabled(false);


        btnSelectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


                final CharSequence[] options = {"Elegir de la galería","Cancelar"};

                builder.setTitle("Selecciona tu imagen: ");
                builder.setItems(options,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion){
                        if((options[seleccion].equals ("Elegir de la galería")) || (options[seleccion].equals("Elegir de la galería"))){

                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Elegir de la galería"),SELECT_PICTURE);
                        }else{
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        btnTrasnformImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                barEntries.clear();
                dataSets.clear();
                mChart.clear();

                for(int a=0; a<256; a++){
                    Histograma[a] = 0;
                }


                if(!etPotencia.getText().toString().isEmpty()){

                    potencia = Double.parseDouble(etPotencia.getText().toString());

                    if(bandera==false){
                        config = imgOriginal.getConfig();
                        imgTransformada = imgOriginal.copy(config, true);
                        bandera = true;
                    }

                    TrasnformarImagen cg = new TrasnformarImagen();
                    cg.execute(opcion);

                }else{
                    Toast.makeText(getBaseContext(),"Faltan datos por llenar", Toast.LENGTH_SHORT).show();
                }


            }
        });//Fin btnTransformImage




        sbUmbral1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvUmbral1.setText(""+progress);
                umbral1 = progress;
                tvUmbral1Color.setBackgroundColor(Color.rgb(progress,progress,progress));
                if(sbUmbral1.getProgress()>=sbUmbral2.getProgress()){
                    sbUmbral2.setProgress(sbUmbral1.getProgress()+1);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });//Fin sbUmbral1

        sbUmbral2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvUmbral2.setText(""+progress);
                umbral2 = progress;
                tvUmbral2Color.setBackgroundColor(Color.rgb(progress,progress,progress));
                if(sbUmbral2.getProgress()<=sbUmbral1.getProgress()){
                    sbUmbral1.setProgress(sbUmbral2.getProgress()-1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });//Fin sbUmbral2


    }//Fin oncreate




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case SELECT_PICTURE:
                if(resultCode == RESULT_OK){

                    //Resetea valores
                    bandera = false;

                    imgOriginal = null;
                    if(imgTransformada!=null){
                        imgTransformada.recycle();
                        imgTransformada = null;
                    }
                    ivOriginal.setImageDrawable(null);
                    ivGray.setImageDrawable(null);

                    txtPorcentaje.setText("0%");
                    barProgreso.setProgress(0);

                    //Carga la ruta y establece la imagen en imageView
                    Uri path = data.getData();
                    ivOriginal.setImageURI(path);
                    imgOriginal = ((BitmapDrawable)ivOriginal.getDrawable()).getBitmap();

                    //Medidas de la imagen
                    width = imgOriginal.getWidth();
                    height = imgOriginal.getHeight();

                    //Comprueba que la imagen haya sido cargada
                    if(width<4096 && height <4096){
                        btnTrasnformImage.setEnabled(true);

                    }else{

                        ivOriginal.setImageDrawable(null);
                        imgOriginal =null;
                        Toast.makeText(getBaseContext(),"La imagen debe ser menor a 4096x4096px", Toast.LENGTH_SHORT).show();
                    }


                }

        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rGris:
                if (checked)
                    opcion="gris";
                    break;
            case R.id.rNegativo:
                if (checked)
                    opcion="negativo";
                    break;
            case R.id.rUmbralSimple:
                if (checked)
                    opcion="umbralSimple";
                    break;
            case R.id.rUmbralCompuesto:
                if (checked)
                    opcion="umbralCompuesto";
                    break;
            case R.id.rAclarar:
                if (checked)
                    opcion="aclarar/oscurecer";
                    break;
        }
        Toast.makeText(getApplicationContext(),opcion, Toast.LENGTH_SHORT).show();
    }



    //Clase para ejecutar la tranformacion
    private class TrasnformarImagen extends AsyncTask<String, Integer, Void> {
        int progreso;

        @Override
        protected void onPreExecute() {

            //Resetea Valores
            ivGray.setImageDrawable(null);
            txtPorcentaje.setText("0%");
            barProgreso.setProgress(0);
            btnTrasnformImage.setEnabled(false);
            //enEjecucion = true;
            progreso = 0;


        }

        @Override
        protected Void doInBackground(String... params) {

            String option = params[0];

            //Convertir a gris
            for (int x = 0; x < width - 1; x++) {
                for (int y = 0; y < height - 1; y++) {

                    int pixel = imgOriginal.getPixel(x, y);
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);

                    int nuevoPixel = (int) (red + green + blue) / 3;

                    switch (option) {
                        case "gris":
                            imgTransformada.setPixel(x, y, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));
                            break;

                        case "negativo":

                            nuevoPixel = 255 - nuevoPixel;
                            imgTransformada.setPixel(x, y, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));
                            break;

                        case "umbralSimple":

                            if (nuevoPixel <= umbral1) {
                                nuevoPixel = 0;
                            } else {
                                nuevoPixel = 255;
                            }
                            imgTransformada.setPixel(x, y, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));
                            break;

                        case "umbralCompuesto":

                            if (nuevoPixel >= umbral1 && nuevoPixel <= umbral2) {
                                nuevoPixel = nuevoPixel;
                            } else {
                                nuevoPixel = 255;
                            }

                            imgTransformada.setPixel(x, y, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));
                            break;
                        case "aclarar/oscurecer":

                            nuevoPixel = (int) ((Math.pow((Double.valueOf(nuevoPixel) / 255), potencia)) * 255);
                            imgTransformada.setPixel(x, y, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));
                            break;
                    }
                }
                //Actualiza el valor de la barra de progreso
                int valor = (int)((x* 100)/width);

                publishProgress(valor);

            }
            publishProgress(100);
            return null;
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            barProgreso.setProgress(values[0]);
            txtPorcentaje.setText(""+values[0]+"%");
        }


        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getBaseContext(),"La imagen ha sido transformada", Toast.LENGTH_SHORT).show();

            //Activa botones cuando termina de encriptar
            ivGray.setImageBitmap(imgTransformada);

            for (int x = 0; x <width-1; x++) {
                for (int y = 0; y <height-1; y++) {

                    int pixel = imgTransformada.getPixel(x, y);
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);

                    int valor =((red+green+blue)/3);
                    Histograma[valor]=Histograma[valor]+1;

                    //txtHistograma.setText(""+red);
                }
            }
            txtHistograma.setText("Histograma: \n"+ "Width: "+width+ "\n Height: "+height+"\n No. pixeles: "+width*height);

            for(int a=0; a<256; a++){
                //txtHistograma.setText(txtHistograma.getText().toString() +a+"- "+Histograma[a]+"\n");
                barEntries.add(new BarEntry(a+1,Histograma[a]));
            }


            //Para las etiquetas en x
            String[] numeros = new String[257];
            for(int a=1; a<=256; a++){
                numeros[a] = String.valueOf(a-1).toString();
            }



            barDataSet = new BarDataSet(barEntries,"Valores");
            dataSets.add((IBarDataSet) barDataSet);

            BarData data = new BarData(barDataSet);
            mChart.setData(data);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            //xAxis.setCenterAxisLabels(true);
            xAxis.setDrawGridLines(false);
            //xAxis.setAxisMaximum(6);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(numeros));

            btnTrasnformImage.setEnabled(true);
            //enEjecucion = false;
        }
    }//Fin encImagenAsinc


}
