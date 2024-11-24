package lk.ijse.Crop_monitoring_system.Service.ServiceImpl;

import jakarta.transaction.Transactional;
import lk.ijse.Crop_monitoring_system.Dto.CropDTO;
import lk.ijse.Crop_monitoring_system.Dto.Status.CropStatus;
import lk.ijse.Crop_monitoring_system.Entity.CropEntity;
import lk.ijse.Crop_monitoring_system.Entity.FieldEntity;
import lk.ijse.Crop_monitoring_system.Exception.DataPersistException;
import lk.ijse.Crop_monitoring_system.Repository.CropRepo;
import lk.ijse.Crop_monitoring_system.Repository.FieldRepo;
import lk.ijse.Crop_monitoring_system.Service.CropService;
import lk.ijse.Crop_monitoring_system.util.Mapping;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class CropSerrviceImpl implements CropService {
    @Autowired
    private CropRepo cropRepo;
    @Autowired
    private Mapping mapping;
    @Autowired
    private FieldRepo fieldRepo;

    @Override
    public void saveCrop(CropDTO cropDto) {
        try {
            // Map CropDTO to CropEntity
            CropEntity cropEntity = mapping.toCropEntity(cropDto);

            // If there's a field associated, set it
            if (cropDto.getField() != null) {
                FieldEntity fieldEntity = fieldRepo.findById(cropDto.getField()).orElseThrow(() -> new DataPersistException("Field not found with ID: " + cropDto.getField()));
                cropEntity.setField(fieldEntity);
            }

            // Save the CropEntity
            cropRepo.save(cropEntity);
        } catch (Exception e) {
            throw new DataPersistException("Crop could not be saved: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteCrop(Long cropCode) {
        // Check if the Field exists before deletion
        Optional<CropEntity> foundCrop = cropRepo.findById(cropCode);
        if (foundCrop.isEmpty()) {
            throw new DataPersistException("Crop not found with ID: " + cropCode);
        }
        try {
            cropRepo.delete(foundCrop.get());
        } catch (Exception e) {
            throw new DataPersistException("Failed to delete crop with ID: " + cropCode, e);
        }
    }

    @Override
    public CropStatus getCrop(Long cropCode) {
        if (cropRepo.existsById(cropCode)) {
            CropEntity selectedCrop = cropRepo.getReferenceById(cropCode);
            return mapping.toCropDTO(selectedCrop);
        } else {
            /* return new SelectedErrorStatus(2, "Selected Field not found");*/
            throw new DataPersistException("Field not found");

        }
    }


}