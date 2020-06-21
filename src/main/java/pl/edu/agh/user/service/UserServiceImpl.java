package pl.edu.agh.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.agh.file.service.FileMetadataService;
import pl.edu.agh.share.service.SharedFileMetadataService;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.domain.UserRole;
import pl.edu.agh.user.domain.form.UserCreateForm;
import pl.edu.agh.user.domain.form.UserEditForm;
import pl.edu.agh.user.exception.UserNotFoundException;
import pl.edu.agh.user.repository.UserRepository;
import pl.edu.agh.util.CloudIdGenerator;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SharedFileMetadataService sharedFileMetadataService;
    private final FileMetadataService fileMetadataService;
    private final CloudIdGenerator cloudIdGenerator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CloudIdGenerator cloudIdGenerator,
                           SharedFileMetadataService sharedFileMetadataService, FileMetadataService fileMetadataService) {
        this.sharedFileMetadataService = sharedFileMetadataService;
        this.fileMetadataService = fileMetadataService;
        this.userRepository = userRepository;
        this.cloudIdGenerator = cloudIdGenerator;
    }

    @Override
    public User create(UserCreateForm form) {
        log.debug("Creating new user - " + form.getEmail());

        User user = new User();
        user.setCloudId(cloudIdGenerator.generateCloudId());
        user.setEmail(form.getEmail());
        user.setName(form.getName());
        user.setSurname(form.getSurname());
        user.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }

    @Override
    public User edit(UserEditForm form) {
        log.debug("Editing user - ID: " + form.getId());

        User user = userRepository.getOne(form.getId());
        user.setName(form.getName());
        user.setSurname(form.getSurname());
        if(form.getPassword() != null && !form.getPassword().isEmpty()) {
            user.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public User editRole(long userId, UserRole role) throws UserNotFoundException {
        log.debug("Editing user role - ID: " + userId);

        User user = userRepository.getOne(userId);
        if (user == null) {
            throw new UserNotFoundException(Long.toString(userId));
        }

        user.setRole(role);

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(long userId) throws UserNotFoundException {
        User user = userRepository.getOne(userId);
        if (user == null) {
            throw new UserNotFoundException(Long.toString(userId));
        }

        fileMetadataService.deleteAllUserFiles(user);
        sharedFileMetadataService.cancelAllUserShare(user);
        userRepository.delete(userId);
    }
}