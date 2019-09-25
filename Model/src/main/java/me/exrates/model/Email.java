package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Email {

    private String to;
    private String from;
    private String message;
    private String subject;
    private Properties properties;
    private List<Attachment> attachments = new ArrayList<>();

    public static class Attachment {
        private String name;
        private Resource resource;
        private String contentType;

        public Attachment() {
        }

        public Attachment(String name, Resource resource, String contentType) {
            this.name = name;
            this.resource = resource;
            this.contentType = contentType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Resource getResource() {
            return resource;
        }

        public void setResource(Resource resource) {
            this.resource = resource;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }
}
