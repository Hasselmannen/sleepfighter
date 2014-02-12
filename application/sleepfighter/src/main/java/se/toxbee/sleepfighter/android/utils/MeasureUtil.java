/*
 * Copyright 2014 toxbee.se
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.toxbee.sleepfighter.android.utils;

import android.content.Context;

/**
 * MeasureUtil provides utilities for measurement.
 *
 * @author Centril<twingoow@gmail.com> / Mazdak Farrokhzad.
 * @version 1.0
 * @since Nov 11, 2013
 */
public class MeasureUtil {
	public static float dpFromPx( Context ctx, float px ) {
		return px / ctx.getResources().getDisplayMetrics().density;
	}

	public static float pxFromDp( Context ctx, float dp ) {
		return dp * ctx.getResources().getDisplayMetrics().density;
	}

	public static float pxToSp( Context ctx, float px ) {
	    return px / ctx.getResources().getDisplayMetrics().scaledDensity;
	}
}
