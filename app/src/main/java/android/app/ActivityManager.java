/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ActivityManager {

    /**
     * Information you can retrieve about a particular task that is currently
     * "running" in the system.  Note that a running task does not mean the
     * given task actually has a process it is actively running in; it simply
     * means that the user has gone to it and never closed it, but currently
     * the system may have killed its process and is only holding on to its
     * last state in order to restart it when the user returns.
     */
    public static class RunningTaskInfo extends TaskInfo implements Parcelable {

        /**
         * A unique identifier for this task.
         *
         * @deprecated As of {@link android.os.Build.VERSION_CODES#Q}, use
         * {@link RunningTaskInfo#taskId}.
         */
        @Deprecated
        public int id;

        /**
         * Thumbnail representation of the task's current state.
         *
         * @deprecated As of {@link android.os.Build.VERSION_CODES#Q}, currently always null.
         */
        @Deprecated
        public Bitmap thumbnail;

        /**
         * Description of the task's current state.
         *
         * @deprecated As of {@link android.os.Build.VERSION_CODES#Q}, currently always null.
         */
        @Deprecated
        public CharSequence description;

        /**
         * Number of activities that are currently running (not stopped and persisted) in this task.
         *
         * @deprecated As of {@link android.os.Build.VERSION_CODES#Q}, currently always 0.
         */
        @Deprecated
        public int numRunning;

        protected RunningTaskInfo(Parcel in) {
            id = in.readInt();
            thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
            numRunning = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeParcelable(thumbnail, flags);
            dest.writeInt(numRunning);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<RunningTaskInfo> CREATOR = new Creator<RunningTaskInfo>() {
            @Override
            public RunningTaskInfo createFromParcel(Parcel in) {
                return new RunningTaskInfo(in);
            }

            @Override
            public RunningTaskInfo[] newArray(int size) {
                return new RunningTaskInfo[size];
            }
        };
    }
}
