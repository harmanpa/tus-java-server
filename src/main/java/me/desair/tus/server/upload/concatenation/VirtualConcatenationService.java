package me.desair.tus.server.upload.concatenation;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.desair.tus.server.exception.UploadNotFoundException;
import me.desair.tus.server.upload.UploadInfo;
import me.desair.tus.server.upload.UploadStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link UploadConcatenationService} implementation that uses the file system to keep track
 * of concatenated uploads. The concatenation is executed "virtually" meaning that upload bytes
 * are not duplicated to the final upload but "concatenated" on the fly.
 */
public class VirtualConcatenationService implements UploadConcatenationService {

    private static final Logger log = LoggerFactory.getLogger(VirtualConcatenationService.class);

    private UploadStorageService uploadStorageService;

    public VirtualConcatenationService(final UploadStorageService uploadStorageService) {
        this.uploadStorageService = uploadStorageService;
    }

    @Override
    public void merge(UploadInfo uploadInfo) throws IOException {
        if (uploadInfo != null && uploadInfo.isUploadInProgress()
                && uploadInfo.getConcatenationParts() != null) {

            Long totalLength = 0L;
            Long expirationPeriod = uploadStorageService.getUploadExpirationPeriod();

            for (UploadInfo childInfo : getPartialUploads(uploadInfo)) {

                if (childInfo.isUploadInProgress()) {
                    //One of our partial uploads is still in progress, we can't calculate the total length yet
                    totalLength = null;
                } else {
                    if (totalLength != null) {
                        totalLength += childInfo.getLength();
                    }

                    //Make sure our child uploads do not expire
                    //since the partial child upload is complete, it's safe to update it.
                    if(expirationPeriod != null) {
                        childInfo.updateExpiration(expirationPeriod);
                        updateUpload(childInfo);
                    }
                }
            }

            if(totalLength != null && totalLength > 0) {
                uploadInfo.setLength(totalLength);
                uploadInfo.setOffset(totalLength);

                if(expirationPeriod != null) {
                    uploadInfo.updateExpiration(expirationPeriod);
                }

                updateUpload(uploadInfo);
            }
        }
    }

    @Override
    public InputStream getConcatenatedBytes(UploadInfo uploadInfo) throws IOException {
        merge(uploadInfo);

        if (uploadInfo == null || uploadInfo.isUploadInProgress()) {
            return null;
        } else {
            List<UploadInfo> uploads = getPartialUploads(uploadInfo);
            return new SequenceInputStream(new UploadInputStreamEnumeration(uploads, uploadStorageService));
        }
    }

    @Override
    public List<UploadInfo> getPartialUploads(UploadInfo info) throws IOException {
        List<UUID> concatenationParts = info.getConcatenationParts();

        if (concatenationParts == null || concatenationParts.isEmpty()) {
            return Collections.singletonList(info);
        } else {
            List<UploadInfo> output = new ArrayList<>(concatenationParts.size());
            for (UUID childId : concatenationParts) {
                output.add(uploadStorageService.getUploadInfo(childId));
            }
            return output;
        }
    }

    private void updateUpload(UploadInfo uploadInfo) throws IOException {
        try {
            uploadStorageService.update(uploadInfo);
        } catch (UploadNotFoundException e) {
            log.warn("Unexpected exception occurred while saving upload info with ID " + uploadInfo.getId(), e);
        }
    }

}
