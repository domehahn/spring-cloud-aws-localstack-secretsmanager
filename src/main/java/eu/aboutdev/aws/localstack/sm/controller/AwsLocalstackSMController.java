package eu.aboutdev.aws.localstack.sm.controller;

import eu.aboutdev.aws.localstack.sm.transfer.SecretRequest;
import eu.aboutdev.aws.localstack.sm.transfer.SecretResponse;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AwsLocalstackSMController {

    private final SecretsManagerClient secretsManagerClient;

    public AwsLocalstackSMController(final SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
    }

    @GetMapping("/listSecrets")
    public SecretResponse listSecrets() {
        final ListSecretsResponse listSecretsResponse = secretsManagerClient.listSecrets();
        final List<SecretListEntry> secretListEntries = listSecretsResponse.secretList();
        final List<String> secrets = secretListEntries.stream()
                .map(SecretListEntry::arn)
                .collect(Collectors.toList());
        return new SecretResponse(secrets);
    }

    @GetMapping("/getSecret/{secretId}")
    public String getSecretValue(@PathVariable String secretId) {
        final GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretId)
                .build();
        final GetSecretValueResponse getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);

        return getSecretValueResponse.secretString();
    }

    @PostMapping("/createSecret")
    public String createSecret(@RequestBody SecretRequest secretRequest) {
        final CreateSecretRequest createSecretRequest = CreateSecretRequest.builder()
                .name(secretRequest.name())
                .secretString(secretRequest.value())
                .build();
        final CreateSecretResponse createSecretResponse = secretsManagerClient.createSecret(createSecretRequest);

        return createSecretResponse.arn();
    }

    @DeleteMapping("/deleteSecret/{secretId}")
    public String deleteSecret(@PathVariable String secretId) {
        final DeleteSecretRequest deleteSecretRequest = DeleteSecretRequest.builder()
                .secretId(secretId)
                .forceDeleteWithoutRecovery(true)
                .build();
        final DeleteSecretResponse deleteSecretResponse = secretsManagerClient.deleteSecret(deleteSecretRequest);

        return deleteSecretResponse.arn();
    }
}
