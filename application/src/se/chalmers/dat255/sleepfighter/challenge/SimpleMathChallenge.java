/*******************************************************************************
 * Copyright (c) 2013 See AUTHORS file.
 * 
 * This file is part of SleepFighter.
 * 
 * SleepFighter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SleepFighter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SleepFighter. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package se.chalmers.dat255.sleepfighter.challenge;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

import se.chalmers.dat255.sleepfighter.R;
import se.chalmers.dat255.sleepfighter.activity.ChallengeActivity;
import se.chalmers.dat255.sleepfighter.challenge.math.DifferentiationProblem;
import se.chalmers.dat255.sleepfighter.challenge.math.IntegrationProblem;
import se.chalmers.dat255.sleepfighter.challenge.math.MathProblem;
import se.chalmers.dat255.sleepfighter.challenge.math.MatrixProblem;
import se.chalmers.dat255.sleepfighter.challenge.math.PrimeFactorizationProblem;
import se.chalmers.dat255.sleepfighter.utils.debug.Debug;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * A Class for randomly generating simple arithmetic challenges.
 * 
 * @author Laszlo Sall Vesselenyi, Danny Lam, Johan Hasselqvist
 */

public class SimpleMathChallenge implements Challenge {
	
	MathProblem problem;
	

	public void runChallenge() {
		this.problem.newProblem();
	}

	/**
	 * An activity with the math layout; 
	 * TextView, EditText and an answer-Button.
	 * 
	 */
	
	@Override
	public void start(final ChallengeActivity activity) {
		
		// TODO: randomize math challenge
		problem = new PrimeFactorizationProblem();
		
		activity.setContentView(R.layout.alarm_challenge_math);
		runChallenge();

		final EditText editText = (EditText) activity
				.findViewById(R.id.answerField);
		
		
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					handleAnswer(editText, activity);
					handled = true;
				}
				return handled;
			}
		});

		// make the keyboard appear.
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	
		setupWebview(activity);
		renderMathProblem(activity);
		
		// Function of 1 variable, keep track of 3 derivatives with respect to that variable,
		// use 2.5 as the current value.  Basically, the identity function.
		DerivativeStructure x = new DerivativeStructure(1, 3, 0, 2.5);
		// Basically, x --> x^2.
		DerivativeStructure x2 = x.pow(2);
		//Linear combination: y = 4x^2 + 2x
		DerivativeStructure y = new DerivativeStructure(4.0, x2, 2.0, x);
		System.out.println("y    = " + y.getValue());
		System.out.println("y'   = " + y.getPartialDerivative(1));
		System.out.println("y''  = " + y.getPartialDerivative(2));
		System.out.println("y''' = " + y.getPartialDerivative(3));
	}
	
	public static String open_html =
"<!DOCTYPE html><html lang=\"en\" xmlns:m=\"http://www.w3.org/1998/Math/MathML\"><head><meta charset=\"utf-8\"><link rel=\"stylesheet\" href=\"file:///android_asset/jqmath-0.4.0.css\"><script src=\"file:///android_asset/jquery-1.4.3.min.js\"></script><script src=\"file:///android_asset/jqmath-etc-0.4.0.min.js\"></script></head><html>";
public static String close_html = "</html>";
	
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi", "InlinedApi" })
	public void setupWebview(final ChallengeActivity activity) {
		final WebView w = (WebView)  activity.findViewById(R.id.math_webview);
		w.getSettings().setJavaScriptEnabled(true);
		
		// make rendering faster.
		w.getSettings().setRenderPriority(RenderPriority.HIGH);
		w.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		if(Build.VERSION.SDK_INT >= 11)
			w.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	
	public void renderMathProblem(final ChallengeActivity activity) {
		final WebView w = (WebView)  activity.findViewById(R.id.math_webview);
		
		String problem = this.problem.render();
		
		String html = new StringBuilder().append(open_html).append(problem).append(close_html).toString();
		
		w.loadDataWithBaseURL("file:///android_asset", html, "text/html", "utf-8", "");	
	}
	
	
	/**
	 * Handles what will happen when you answer
	 */

	private void handleAnswer(final EditText editText,
			final ChallengeActivity activity) {
		boolean correctAnswer = false;
		try {
			int guess = Integer.parseInt(editText.getText().toString());
			int solution = this.problem.solution();
			Debug.d(guess + "");
			Debug.d(solution + "");
			
			if (guess == solution) {
				activity.complete();
				correctAnswer = true;
				Toast.makeText(activity.getBaseContext(), "Alarm deactivated!",
						Toast.LENGTH_SHORT).show();
			}
		} catch (NumberFormatException e) {
			// Handles exception when the user answer with empty strings
		}
		if (!correctAnswer) {
			// somehow reload here. 
			Toast.makeText(activity.getBaseContext(), "Wrong answer!",
					Toast.LENGTH_SHORT).show();
			runChallenge();
		
			renderMathProblem(activity);
			editText.setText("");
		}
	}
}
