package se.chalmers.dat255.sleepfighter.challenges;

import java.util.Random;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import se.chalmers.dat255.sleepfighter.R;
import se.chalmers.dat255.sleepfighter.activities.ChallengeActivity;

public class SimpleMathChallenge implements Challenge {

	private Random random = new Random();
	private int operand1 = 0;
	private int operand2 = 0;
	private int operation = 0;
	private int result = 0;

	public void runChallenge() {
		nextInts();
		nextOp();
		result();
	}

	private void nextInts() {
		operand1 = random.nextInt(10) + 1;
		operand2 = random.nextInt(10) + 1;
	}

	private void nextOp() {
		operation = random.nextInt(4);
	}

	private void result() {
		if (operation == 0) {
			result = operand1 + operand2;
		} else if (operation == 1) {
			result = operand1 - operand2;
		} else if (operation == 2) {
			result = operand1 * operand2;
		} else if (operation == 3) {
			operand1 = operand1 * operand2;
			result = operand1 / operand2;
		}
	}

	public int getResult() {
		return result;
	}

	public String getCalculation() {
		if (operation == 0) {
			return operand1 + " + " + operand2;
		} else if (operation == 1) {
			return operand1 + " - " + operand2;
		} else if (operation == 2) {
			return operand1 + " * " + operand2;
		} else {
			return operand1 + " / " + operand2;
		}
	}

	@Override
	public void start(final ChallengeActivity activity) {
		activity.setContentView(R.layout.alarm_challenge_math);
		runChallenge();

		final TextView userText = (TextView) activity
				.findViewById(R.id.questionField);

		userText.setText(getCalculation());

		final EditText editText = (EditText) activity
				.findViewById(R.id.answerField);
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (Integer.parseInt(editText.getText().toString()) == getResult()) {
						activity.complete();
					} else {
						Toast.makeText(activity.getBaseContext(),
								"Sorry, wrong answer!", Toast.LENGTH_SHORT)
								.show();
					}
					handled = true;
				}
				return handled;
			}
		});

	}

}
