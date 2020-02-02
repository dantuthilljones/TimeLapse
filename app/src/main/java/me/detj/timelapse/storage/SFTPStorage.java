package me.detj.timelapse.storage;

import android.media.Image;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import me.detj.timelapse.settings.TimeLapseSettings;

public class SFTPStorage {

    public boolean validate(final TimeLapseSettings settings) {
        final Object lock = new Object();
        final AtomicBoolean result = new AtomicBoolean(false);

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        result.set(validateSFTP(settings));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.get();
    }

    private boolean validateSFTP(TimeLapseSettings settings) {
        try {
            Session session = startSession(settings.getHostname(), settings.getUsername(), settings.getPassword());
            session.disconnect();
            return true;
        } catch (JSchException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Session startSession(String hostname, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();
        return session;
    }

    public void store(final Image image, final TimeLapseSettings settings) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    uploadViaSFTP(image, settings);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void uploadViaSFTP(Image image, TimeLapseSettings settings) {
        Session session = null;
        ChannelSftp sftp = null;

        try {
            session = startSession(settings.getHostname(), settings.getUsername(), settings.getPassword());
            sftp = startSFTPChannel(session);

            byte[] bytes = getBytes(image.getPlanes()[0].getBuffer());
            try(InputStream inputStream = new ByteArrayInputStream(bytes)) {
                sftp.put(inputStream, settings.getDirectory() + "/" + generateFilename());
            }
        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        } finally {
            image.close();
            if (sftp != null) {
                sftp.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private ChannelSftp startSFTPChannel(Session session) throws JSchException {
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        return sftp;
    }

    private static byte[] getBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }

    private String generateFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return sdf.format(new Date()) + ".jpg";
    }
}
