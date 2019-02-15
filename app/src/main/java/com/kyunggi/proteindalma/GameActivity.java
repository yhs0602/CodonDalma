package com.kyunggi.proteindalma;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends Activity implements View.OnTouchListener,View.OnClickListener
{
	SharedPreferences pref;
	SharedPreferences.Editor edit; 
	private long currentTime;

	private int consumer;

	private int reader;

	Toast toast;
	int hp=500;
	int score=0;
	ArrayList<Dalma> dalmas=new ArrayList<>();
	ArrayList<TextView> tvDalmas=new ArrayList<>();
	Codon codon=new Codon();
    private ImageView ivOX;
    private int combo=0;
    private TextView tvCombo;
    private boolean showHint=true;
    private TextView tvHint;

    public synchronized void showToast(String s)
	{
		//java.security.AlgorithmConstraints ac;
		if(toast!=null)
		{
			toast.cancel();
		}
		if(isDestroyed())
			return;
		if(isFinishing())
			return;
		try{
			toast=Toast.makeText(this,s,2);
			toast.show();
		}catch(Exception e)
		{
			Log.e("ProDalma","",e);
		}
	}
	public void CreateDalmas()
	{
		dalmas.clear();
		for (int i=0;i < 100;++i)
		{
			dalmas.add(new Dalma(i * 2000, Codon.Protein.randomProtein()));
		}
	}
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		int id=p1.getId();
		char base='0';
		switch (id)
		{
			case R.id.gameButtonU:
				base = 'U';
				break;
			case R.id.gameButtonC:
				base = 'C';
				break;
			case R.id.gameButtonA:
				base = 'A';
				break;
			case R.id.gameButtonG:
				base = 'G';
				break;
			case R.id.gameButtonExit:
				{
					Exit();
					return;
				}
			case R.id.gameImageButtonMenu:
				thread.interrupt();
				llMenu.setVisibility(View.VISIBLE);
				break;
			case R.id.gameButtonResume:
				llMenu.setVisibility(View.INVISIBLE);
				resume();
				break;
			case R.id.gameButtonSave:
				Exit();
				return;
		}
		if (base != '0')
		{
			switch(codon.index)
			{
				case 0:
					tv1.setText(""+base);
					tv1.setBackgroundColor(getBaseColor(base));
					break;
				case 1:
					tv2.setText(""+base);
					tv2.setBackgroundColor(getBaseColor(base));
					break;
				case 2:
					tv3.setText(""+base);
					tv3.setBackgroundColor(getBaseColor(base));
					break;
			}
			codon.AddBase(base);
			if (codon.isDone())
			{
				tv1.setText("");
				tv1.setBackgroundColor(Color.WHITE);
				tv2.setText("");
				tv2.setBackgroundColor(Color.WHITE);
				tv3.setText("");
				tv3.setBackgroundColor(Color.WHITE);
				Codon.Protein protein=codon.getProtein();
				Dalma dalma=dalmas.get(consumer);
				if (dalma.protein == protein)
				{
					TextView tv=tvDalmas.get(consumer);
					tv.setVisibility(View.GONE);
					++consumer;
					dalma.alive = false;
                    combo++;
                    if(combo>1) {
                        tvCombo.setText(combo + "COMBO");
                        tvCombo.setVisibility(View.VISIBLE);
                    }
                    AddScore((int) (50*Math.exp(combo)));
					ivOX.setImageResource(android.R.drawable.star_big_on);

					try
					{
						DamageHP(-20);
					}
					catch (InterruptedException e)
					{}
				}
				else
				{
					try
					{
					    combo=0;
					    tvCombo.setVisibility(View.INVISIBLE);
					    ivOX.setImageResource(android.R.drawable.ic_delete);
						DamageHP(10);
					}
					catch (InterruptedException e)
					{}
					showToast("That's " + getName(protein));
				}			
				codon.reset();
			}
		}
		return ;
	}

	private int getBaseColor(char base)
	{
		// TODO: Implement this method
		int col=0;
		switch(base)
		{
			case 'U':
				col = Color.YELLOW;
				break;
			case 'C':
				col = Color.GREEN;
				break;
			case 'A':
				col=Color.RED;
				break;
			case 'G':
				col=Color.BLACK;
				break;
		}
		return col;
	}

	private void  AddScore(int p0)
	{
		// TODO: Implement this method
		score += p0;
		String fmt=String.format("%04d", score);
		tvScore.setText(fmt);
		return ;
	}

	private void  DamageHP(int p0) throws InterruptedException
	{
		// TODO: Implement this method
		hp -= p0;
		if(hp>500)
			hp=500;
		runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					pbHP.setProgress(hp);
					return ;
				}	
			});
		if (hp < 0)
		{
			hp = 0;
			if(!Thread.currentThread().equals(getMainLooper().getThread()))
			{
				throw new InterruptedException();
			}
			FinishGame();
		}
		return ;
	}

	private void FinishGame()
	{
		// TODO: Implement this method
		pref = getSharedPreferences("save", 0);       //save 라는 그룹이름으로 객체를 가져온다 
		edit = pref.edit();
		final int high=pref.getInt("HighScore", 0);
		if (high < score)
		{
			edit.putInt("HighScore", score);
			edit.commit();
		}
		runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					showToast("Score: " + score + " High Score: " +( high>score?high:score));
					return ;
				}				
			});
		Exit();
		return ;
	}

	private synchronized void Exit()
	{
		if (Thread.currentThread() != thread)
		{
			boolean retry=true;
			thread.interrupt();

			while (retry)
			{			
				//try
				//{
					//thread.join();
					retry = false;
				//}
				//catch (InterruptedException e)
				//{
					break;
				//}
			}
		}else{

		}
		if(!isFinishing())
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						GameActivity.super.onBackPressed();
						return ;
					}						
			});
	}

	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		int id=p1.getId();
		switch (id)
		{
			case R.id.gameButtonU:

		}
		return false;
	}

	Button btU,btC,btA,btG;
	ProgressBar pbHP;
	ImageButton ibMenu;
	Button btResume,btSave,btRetry,btExit;
	TextView tvScore;
	LinearLayout llMenu;
	FrameLayout llDalmas;
	TextView tv1,tv2,tv3;
	MainActivity.NameMode nm;
	int width;
	int height;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		pref=getSharedPreferences("save", 0);
		nm=MainActivity.NameMode.valueOf(pref.getString("NameMode","SHORT"));
		pref=getSharedPreferences("showHint",MODE_PRIVATE);
		showHint=pref.getBoolean("showHint",true);
		tvHint = (TextView) findViewById(R.id.tvHint);
		tvHint.setVisibility(showHint?View.VISIBLE:View.GONE);
		btU = (Button) findViewById(R.id.gameButtonU);
		btC = (Button) findViewById(R.id.gameButtonC);
		btA = (Button) findViewById(R.id.gameButtonA);
		btG = (Button) findViewById(R.id.gameButtonG);
		btU.setOnTouchListener(this);
		btU.setOnClickListener(this);
		btC.setOnTouchListener(this);
		btC.setOnClickListener(this);
		btA.setOnTouchListener(this);
		btA.setOnClickListener(this);
		btG.setOnTouchListener(this);
		btG.setOnClickListener(this);
		ibMenu = (ImageButton) findViewById(R.id.gameImageButtonMenu);
		ibMenu.setOnClickListener(this);
		tvScore = (TextView) findViewById(R.id.gameTextViewScore);
		btRetry = (Button) findViewById(R.id.gameButtonRetry);
		btSave = (Button) findViewById(R.id.gameButtonSave);
		btExit = (Button) findViewById(R.id.gameButtonExit);
		btResume = (Button) findViewById(R.id.gameButtonResume);
		ivOX = (ImageView) findViewById(R.id.imageViewOX);
		tvCombo = (TextView) findViewById(R.id.tvCombo);
		btRetry.setOnClickListener(this);
		btSave.setOnClickListener(this);
		btExit.setOnClickListener(this);
		btResume.setOnClickListener(this);
		pbHP = (ProgressBar) findViewById(R.id.progress_horizontal);
		llMenu = (LinearLayout) findViewById(R.id.gameLinearLayoutMenu);
		llDalmas = (FrameLayout) findViewById(R.id.gameFrameLayoutDalmas);
		tv1=(TextView) findViewById(R.id.gameTextView1);
		tv2=(TextView) findViewById(R.id.gameTextView2);
		tv3=(TextView) findViewById(R.id.gameTextView3);
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
				@Override
				public void uncaughtException(Thread p1, Throwable p2)
				{
					// TODO: Implement this method
					Log.e("ProDalma",p1.getName()+"\n"+p1.getAllStackTraces(),p2);
					return ;
				}
			});
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		start();
		return ;
	}

	@Override
	public void onBackPressed()
	{
		// TODO: Implement this method
		return ;
	}

	public void start()
	{
		reader = 0;
		consumer = 0;
		currentTime = 0;
		combo=0;
		CreateDalmas();
		thread.start();
	}
	public void resume()
	{
		thread.start();
	}
	Thread thread=new Thread(){

		@Override
		public void run()
		{
			// TODO: Implement this method
			long startmills=System.currentTimeMillis();
			try{
			while (!interrupted())
			{
				long delta=System.currentTimeMillis() - startmills;
				DamageHP((int)delta / 10000);
				final Dalma toSpawn=dalmas.get(reader);
				if (toSpawn.spawntime < delta)
				{
					tvDalmas.add(new TextView(GameActivity.this.getApplicationContext()));
					final TextView tv=tvDalmas.get(reader);
					tv.setBackgroundColor(getBGColor(toSpawn.protein.name()));
					tv.setText(GameActivity.this.getName(toSpawn.protein));
					tv.setGravity(Gravity.CENTER);
					tv.setTextColor(Color.BLACK);
					final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
					final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
					final LayoutParams lp=new LayoutParams(width, height);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
					tv.setTranslationY(-50);
					tv.setTranslationX(new Random().nextInt(300));
					runOnUiThread(new Runnable(){
							@Override
							public void run()
							{
								// TODO: Implement this method
								llDalmas.addView(tv, 0, lp);
								if(showHint)
                                {
                                    tvHint.setText("Hint:"+toSpawn.protein.getCodon());
                                }
								return ;
							}
						});


					toSpawn.alive = true;
					++reader;
					if (reader >= dalmas.size())
					{
						break;
					}
				}
				for (int i=0;i < tvDalmas.size();++i)
				{
					Dalma d=dalmas.get(i);
					if (d.alive)
					{
						final TextView tv=tvDalmas.get(i);
						runOnUiThread(new Runnable(){
								@Override
								public void run()
								{
									// TODO: Implement this method
									tv.setTranslationY(tv.getTranslationY() + 10);
									return ;
								}
							});
						
						int y=(int)tv.getTranslationY();
						if (y > height-200)
						{
							final Dalma tmd=d;
							/*runOnUiThread(new Runnable(){
									@Override
									public void run()
									{
										showToast(tmd.protein.getCodon());
										return;
									}			
							});
							*/
							codon.reset();
							DamageHP(10);
							++consumer;
							runOnUiThread(new Runnable(){
									@Override
									public void run()
									{
										// TODO: Implement this method
										tv.setVisibility(View.GONE);
										return ;
									}
								});
							d.alive = false;		
						}
					}
				}
				try
				{
					Thread.sleep((long)50);
				}
				catch (InterruptedException e)
				{
					break;
				}
				
			}
			}catch(InterruptedException e)
			{
				
			}
			FinishGame();
			return ;
		}

		

		private int  getBGColor(String name)
		{
			// TODO: Implement this method
			char r=name.charAt(0);
			char g=name.charAt(1);
			char b=name.charAt(2);
			r = (char)((r - 'A') * 128 / 26+125);
			g = (char)((g - 'A') * 128 / 26+125);
			b = (char)((b - 'A') * 128 / 26+125);
			return Color.rgb(r, g, b);
		}
	};
	private String getName(Codon.Protein protein)
	{
		// TODO: Implement this method
		switch(nm)
		{
			case KOR:
				return protein.getKor();
			case FULL:
				return protein.getFull();
			case SHORT:

		}
		return protein.name();
	}
}
