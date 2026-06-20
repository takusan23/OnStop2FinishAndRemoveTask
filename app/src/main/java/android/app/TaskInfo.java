/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.app;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;

public class TaskInfo {
    /**
     * The identifier for this task.
     */
    public int taskId;
    /**
     * The component of the top activity in the task, currently showing to the user.
     */
    public ComponentName topActivity;
    /**
     * The {@link ActivityInfo} of the top activity in this task.
     *
     * @hide
     */
    public ActivityInfo topActivityInfo;
}