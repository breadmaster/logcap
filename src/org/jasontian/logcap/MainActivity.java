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

package org.jasontian.logcap;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.widget.Toast;

import org.jasontian.logcap.R;
import org.jasontian.logcap.util.FileInfoPreference;
import org.jasontian.logcap.util.MultiSelectListPreference;
import org.jasontian.logcap.util.Util;

/**
 * @author Jason Tian
 */
public class MainActivity extends PreferenceActivity implements
        OnPreferenceChangeListener {

    private CheckBoxPreference mSwitch;

    private MultiSelectListPreference mChooseBuffers;

    private ListPreference mChooseFormat;

    private FileInfoPreference mLogInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.logcap);
        mSwitch = (CheckBoxPreference) findPreference(getText(R.string.switch_key));
        mSwitch.setOnPreferenceChangeListener(this);
        mChooseBuffers = (MultiSelectListPreference) findPreference(getText(R.string.buffer_key));
        String[] buffs = Util.getAvailableBuffers();
        mChooseBuffers.setEntries(buffs);
        mChooseBuffers.setEntryValues(buffs);
        mChooseBuffers.setOnPreferenceChangeListener(this);
        mChooseFormat = (ListPreference) findPreference(getText(R.string.format_key));
        mChooseFormat.setOnPreferenceChangeListener(this);
        mLogInfo = (FileInfoPreference) findPreference(getText(R.string.file_info_key));
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSelectedFormat(null);
        updateSelectedBuffers(null);
        updateLogFileInfo();
    }

    private void updateSelectedFormat(String f) {
        String format = f == null ? mChooseFormat.getValue() : f;
        mChooseFormat.setSummary(getString(R.string.format_summary) + " "
                + format);
    }

    private void updateSelectedBuffers(String b) {
        String buffers = b == null ? mChooseBuffers.getValue() : b;
        StringBuffer sb = new StringBuffer();
        String[] bufs = MultiSelectListPreference.parseStoredValue(buffers);
        if (bufs != null) {
            for (String buf : bufs) {
                sb.append(buf);
                sb.append(", ");
            }
            if (sb.length() > 1) {
                sb.delete(sb.length() - 2, sb.length());
            }
        }
        String summary = getString(R.string.buffer_summary) + " " + sb.toString();
        mChooseBuffers.setSummary(summary);
    }

    private void updateLogFileInfo() {
        mLogInfo.setSummary(Util.getLogFileInfo(this));
    }

    private boolean isInvalidLogSetting(String[] bufs, String format) {
        boolean invalid = false;
        if (bufs == null || bufs.length < 1) {
            invalid = true;
        } else if (TextUtils.isEmpty(format)) {
            invalid = true;
        }
        return invalid;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSwitch) {
            Intent service = new Intent()
                    .setClass(getApplicationContext(), LogcapService.class);
            if ((Boolean) newValue) {
                String[] bufs = MultiSelectListPreference.parseStoredValue(mChooseBuffers
                        .getValue());
                String format = mChooseFormat.getValue();
                if (isInvalidLogSetting(bufs, format)) {
                    Toast.makeText(getApplicationContext(), R.string.msg_invalid_buffer_or_format,
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                service.putExtra(Util.EXTRA_START, true);
                service.putExtra(Util.EXTRA_BUFFER, bufs);
                service.putExtra(Util.EXTRA_FORMAT, format);
            }
            startService(service);
            // FIXME not working
            updateLogFileInfo();
            return true;
        } else if (preference == mChooseFormat) {
            updateSelectedFormat(newValue.toString());
            return true;
        } else if (preference == mChooseBuffers) {
            updateSelectedBuffers(newValue.toString());
            return true;
        }
        return false;
    }
}
