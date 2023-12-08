package com.madnan.pfbackenddemo.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class FlowableProcessConfiguration {

    private final RepositoryService repositoryService;

    @Value("classpath:processes/*")
    private Resource[] resources;

    @PostConstruct
    @SneakyThrows
    public void deployProcesses() {
        Set<String> filenames;
        filenames = listFileUsingFileWalk();
        if (!CollectionUtils.isEmpty(filenames)) {
            filenames.forEach(key -> {
                try {
                    deployProcessIfChanged(key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Set<String> listFileUsingFileWalk() {
        HashSet<String> filenames = new HashSet<>();
        for (final Resource res : resources) {
            String filename = removeExtensionFromFilename(res.getFilename());
            filenames.add(filename);
        }
        return filenames;
    }

    private String removeExtensionFromFilename(String filename) {
        if (filename == null) {
            return "";
        }
        int index = filename.indexOf('.');
        if (index != -1) {
            return filename.substring(0, index);
        }
        return filename;
    }

    private void deployProcessIfChanged(String key) throws IOException {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService service = processEngine.getRepositoryService();
        boolean isExist = service.createProcessDefinitionQuery().processDefinitionKey(key).count() > 0;
        if (!isExist) {
            repositoryService.createDeployment()
                    .addClasspathResource("processes/" + key + ".bpmn20.xml")
                    .deploy();
        }
    }
}
