/*
 *   LogCap: Capture system logs to files.
 *   Copyright (C) 2012  Jason Tian
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jasontian.logcap.util;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jasontian.logcap.R;

import java.io.IOException;

/**
 * @author Jason Tian
 */
public class BufferInfoPreference extends ListPreference {

    private final String[] BUFFERS;

    public BufferInfoPreference(Context context) {
        this(context, null);
    }

    public BufferInfoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        BUFFERS = Util.getAvailableBuffers();
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        builder.setTitle(R.string.buffer_tip);
        builder.setPositiveButton(R.string.clear_all, this);
        builder.setNegativeButton(R.string.close, null);
        builder.setAdapter(new BufferInfoAdapter(), this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (BUFFERS == null) {
            return;
        }
        if (which < 0) {
            try {
                for (String buf : BUFFERS) {
                    Util.clearBuffer(buf);
                }
            } catch (IOException e) {
                String msg = getContext().getString(R.string.msg_clear_buf_failed);
                Log.w(Util.TAG, msg, e);
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        } else {
            String buf = BUFFERS[which];
            try {
                Util.clearBuffer(buf);
            } catch (IOException e) {
                String msg = getContext().getString(R.string.msg_clear_buf_failed);
                Log.w(Util.TAG, msg, e);
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class BufferInfoAdapter extends BaseAdapter {

        private static final int BUFFER_INFO = 0;

        private static final int EMPTY_VIEW = 1;

        @Override
        public int getItemViewType(int position) {
            return BUFFERS == null ? EMPTY_VIEW : BUFFER_INFO;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return BUFFERS == null ? 1 : BUFFERS.length;
        }

        @Override
        public Object getItem(int position) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == EMPTY_VIEW) {
                convertView = ((LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.general_text, null);
                ((TextView) convertView).setText(R.string.msg_buf_info_failed);
                return convertView;
            }
            if (convertView == null) {
                convertView = ((LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.general_text, null);
            }
            try {
                String info = Util.getBufferInfo(BUFFERS[position]);
                if (info != null) {
                    ((TextView) convertView).setText(
                            Util.getSpannedBufferInfo(info.trim()));
                }
            } catch (IOException e) {
                String msg = getContext().getString(R.string.msg_buf_info_failed);
                Log.w(Util.TAG, msg, e);
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
            return convertView;
        }
    }
}
