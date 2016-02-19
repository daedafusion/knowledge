package com.daedafusion.knowledge.partition.framework;

import com.daedafusion.knowledge.partition.Partition;
import com.daedafusion.knowledge.partition.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.partition.framework.exceptions.StorageException;

import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 7/10/14.
 */
public interface PartitionStorage
{
    enum Action { Read, Write, Admin }

    Partition createPartition(Partition partition) throws StorageException;

    void deletePartition(String uuid) throws ObjectNotFoundException, StorageException;

    Partition getPartition(String uuid) throws ObjectNotFoundException, StorageException;

    void updatePartition(Partition partition) throws ObjectNotFoundException, StorageException;

    List<Partition> getAllPartitions() throws StorageException;

    List<Partition> getPartitions(Action action, Set<String> capabilities) throws StorageException;

    List<Partition> getPartitions(Action action, Set<String> capabilities, Set<String> tags) throws StorageException;

    List<Partition> getPartitions(Action action, Set<String> capabilities, Set<String> tags, Set<String> systemTags) throws StorageException;

//    List<PromotionRequest> getUserPromotionRequests(String user, String domain);
//
//    List<PromotionRequest> getPendingPromotionRequests(String domain);
//
//    void createPromotionRequest(PromotionRequest request);
//    void cancelPromotionRequest(String uuid);
//    void deletePromotionRequest(String uuid);
//    void executePromotionRequest(String uuid);
}
