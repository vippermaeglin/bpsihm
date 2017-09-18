package com.example.first;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.first.dummy.DummyContent;

import android.R.drawable;
import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.sax.RootElement;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.VideoView;

public class ItemDetailFragment extends Fragment {

	public ItemListActivity MainActivity;
    public static final String ARG_ITEM_ID = "item_id";
    
    public View rootView;
    private AudioManager audioManager = null;
    private SeekBar volumeSeekbar = null;
    SharedPreferences preferences;// = PreferencesManager.getDefaultSharedPreferences(context);
    private VideoView videoView1 = null;
    private MediaController mediaController1 = null;
    private volatile Thread runner;

    public boolean connected = false;

    DummyContent.DummyItem mItem;

    public ItemDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        if (mItem != null) {
            if(mItem.content == "Principal") 
        	{
            	rootView=inflater.inflate(R.layout.main, container, false);
            	((EditText)rootView.findViewById(R.id.etMensagens)).setText("Aguardando Envio...");
            	//Buttons Events:
            	((Button)rootView.findViewById(R.id.btIHM01)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btIHM02)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btIHM03)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btIHM04)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btIHM05)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btIHM06)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btIHM07)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btIHM08)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btIHM09)).setOnTouchListener(touchIHM);
            	((Button)rootView.findViewById(R.id.btEnviar)).setOnClickListener(sendIHM);
        	}
            else if(mItem.content == "Configurações")
            {
            	rootView=inflater.inflate(R.layout.settings, container, false);
            	//Load configuration
            	((RadioButton)rootView.findViewById(R.id.rbConexao2)).setChecked(true);
            	InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter() {
               		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
						 if (end > start) {
	                            String destTxt = dest.toString();
	                            String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
	                            if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) { 
	                                return "";
	                            } else {
	                                String[] splits = resultingTxt.split("\\.");
	                                for (int i=0; i<splits.length; i++) {
	                                    if (Integer.valueOf(splits[i]) > 255) {
	                                        return "";
	                                    }
	                                }
	                            }
	                        }
						return null;
					}
                };
                ((EditText)rootView.findViewById(R.id.etIP)).setFilters(filters);
                ((EditText)rootView.findViewById(R.id.etIP)).setText("192.168.0.103");
                ((EditText)rootView.findViewById(R.id.etID)).setText("00");
                ((EditText)rootView.findViewById(R.id.etEstacoes)).setText("10");
            	((SeekBar)rootView.findViewById(R.id.sbLuminosidade)).setMax(100);
            	((SeekBar)rootView.findViewById(R.id.sbLuminosidade)).setProgress(100);
            	((SeekBar)rootView.findViewById(R.id.sbVolume)).setMax(100);
            	((SeekBar)rootView.findViewById(R.id.sbVolume)).setProgress(100);
            	((SeekBar)rootView.findViewById(R.id.sbLuminosidade)).setOnSeekBarChangeListener( 
            			new SeekBar.OnSeekBarChangeListener() {

            				 public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            				  // TODO Auto-generated method stub
            				  float BackLightValue = (float)arg1/100;
            				  if(BackLightValue<0.01f) BackLightValue=0.01f;

            				  WindowManager.LayoutParams layoutParams = MainActivity.getWindow().getAttributes();
            				  layoutParams.screenBrightness = BackLightValue;
            				  MainActivity.getWindow().setAttributes(layoutParams);


            				 }

            				 public void onStartTrackingTouch(SeekBar arg0) {
            				  // TODO Auto-generated method stub

            				 }

            				 public void onStopTrackingTouch(SeekBar arg0) {
            				  // TODO Auto-generated method stub

				 }}); // TODO Auto-generated method stub
                audioManager = (AudioManager) MainActivity.getSystemService(Context.AUDIO_SERVICE);
                volumeSeekbar = (SeekBar)rootView.findViewById(R.id.sbVolume);
                volumeSeekbar.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
                volumeSeekbar.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_SYSTEM));   
                volumeSeekbar.setOnSeekBarChangeListener( 
            			new SeekBar.OnSeekBarChangeListener() {
            				

                            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) 
                            {
                                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                                        progress, 0);
                            }

            				 public void onStartTrackingTouch(SeekBar arg0) {
            				  // TODO Auto-generated method stub

            				 }

            				 public void onStopTrackingTouch(SeekBar arg0) {
            				  // TODO Auto-generated method stub

				 }}); // TODO Auto-generated method stub
						  
            }
            else if(mItem.content == "Sobre")
            {
            	rootView=inflater.inflate(R.layout.about, container, false);
            	//((WebView)rootView.findViewById(R.id.webView1)).loadUrl("http://www.bestcharger.com.br/site/aplicacoes.htm");
            	((WebView)rootView.findViewById(R.id.webView1)).loadUrl("file:///android_asset/html/BESTCHARGER.html");
            }
            else if(mItem.content == "Publicidade")
            {
            	rootView=inflater.inflate(R.layout.marketing, container, false);

                videoView1 = (VideoView)rootView.findViewById(R.id.videoView1);
                mediaController1 = new MediaController(MainActivity);
                //mediaController1 = (MediaController)rootView.findViewById(R.id.mediaController1);
            	((EditText)rootView.findViewById(R.id.etMensagens2)).setText("Aguardando Envio...");
            	//String uri = "android.resource://"+MainActivity.getPackageName()+"/raw/clinade";
            	String uri = "/sdcard/Movies/clinade.mp4";
            	//String uri = "http://www.youtube.com/watch?v=aRdoMKmtzv0";
            	mediaController1.setMediaPlayer(videoView1);
            	mediaController1.setAnchorView(videoView1);
            	mediaController1.setPadding(350, 0, 0, 0);
            	videoView1.setMediaController(mediaController1);
            	videoView1.setVideoURI(Uri.parse(uri));//Video Loop
                videoView1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        videoView1.start(); //need to make transition seamless.
                    }
                });
            	videoView1.start()	;
            	
            }
            else ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.content);
        }
        return rootView;
    }
    private OnTouchListener touchIHM = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN)
                v.setBackgroundResource(R.drawable.ihm_generic_off);
				
				ChooseIHM(v.getId());
            if(event.getAction()==MotionEvent.ACTION_UP)
            	 v.setBackgroundResource(R.drawable.ihm_generic);
			return false;
		}
    };
    public void ChooseIHM(int idIHM)
    {
    	if(idIHM==R.id.btIHM01) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("01");
    	}
    	else if(idIHM==R.id.btIHM02) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("02");
    	}
    	else if(idIHM==R.id.btIHM03) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("03");
    	}
    	else if(idIHM==R.id.btIHM04) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("04");
    	}
    	else if(idIHM==R.id.btIHM05) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("05");
    	}
    	else if(idIHM==R.id.btIHM06) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("06");
    	}
    	else if(idIHM==R.id.btIHM07) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("07");
    	}
    	else if(idIHM==R.id.btIHM08) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("08");
    	}
    	else if(idIHM==R.id.btIHM09) 
    	{
    		EditText etEstacao=(EditText)MainActivity.findViewById(R.id.etEstacao);
    		etEstacao.setText("09");
    	}
    }
    private View.OnClickListener sendIHM = new View.OnClickListener() {
		public void onClick(View v) {
			int ihmDestino=-1;
			try
			{
				if(((EditText)MainActivity.findViewById(R.id.etEstacao)).getText().toString()!="")ihmDestino = Integer.parseInt(((EditText)MainActivity.findViewById(R.id.etEstacao)).getText().toString());
				System.out.print("\nDEBUG: ");
				System.out.println(ihmDestino);
				if(ihmDestino>0 && ihmDestino<10)
				{
				    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity);
				    String msg = "Deseja enviar cápsula para IHM-"+ihmDestino+"?\n\""+((EditText)MainActivity.findViewById(R.id.etDescricao)).getText().toString()+"\"";
				    builder.setMessage(msg)
				           .setCancelable(false)
				           .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {
				                    //TODO: Send TCP message
			                       //Thread cThread = new Thread(new ClientThread());
			                       //cThread.start();
				            	   startThread();
				            	   AlertDialog.Builder builderOK = new AlertDialog.Builder(MainActivity);
								   builderOK.setMessage("Envio Concluído!")
								           .setCancelable(false)
								           .setPositiveButton("OK",new DialogInterface.OnClickListener(){
								               public void onClick(DialogInterface dialog, int id){}});
								   AlertDialog alertOK = builderOK.create();
								   alertOK.show();
				            	   ((EditText)MainActivity.findViewById(R.id.etEstacao)).setText("");
				            	   ((EditText)MainActivity.findViewById(R.id.etDescricao)).setText("");
				               }
				           })
				           .setNegativeButton("Não", new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {
				                    dialog.cancel();
				               }
				           });
				    AlertDialog alert = builder.create();
				    alert.show();
				}
				else 
				{				
					//TODO: Message Error Dialog
					AlertDialog.Builder builderNOK = new AlertDialog.Builder(MainActivity);
					   builderNOK.setMessage("Favor digitar um endereço válido!")
					           .setCancelable(false)
					           .setPositiveButton("OK",new DialogInterface.OnClickListener(){
					               public void onClick(DialogInterface dialog, int id){}});
					   AlertDialog alertOK = builderNOK.create();
					   alertOK.show();
				}
			}
			catch(Exception ex){}
			
		}
	};
	public synchronized void startThread(){
	  if(runner == null){
	    runner = new Thread(new ClientThread());
	    runner.start();
	  }
	}
	public synchronized void stopThread(){
	  if(runner != null){
	    Thread moribund = runner;
	    runner = null;
	    moribund.interrupt();
	  }
	}
	public class ClientThread implements Runnable {

        public void run() {
            try {
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = new Socket("192.168.2.254", 1000);
                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                    .getOutputStream())), true);
                        	String msg = "\n\nCapsula enviada da IHM-00 para IHM-" + ((EditText)MainActivity.findViewById(R.id.etEstacao)).getText().toString()+": "+((EditText)MainActivity.findViewById(R.id.etDescricao)).getText().toString();
                            out.println(msg);
                        	Log.d("ClientActivity", "C: Sent.");
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                    connected=false;
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
            stopThread();
            
        }
    }

}
