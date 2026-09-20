package io.meen.apollo.domain.model;

import io.meen.apollo.BaseTest;
import io.meen.common.model.OperationStatus;

import br.com.six2six.fixturefactory.Fixture;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OperationTest extends BaseTest {

    @Test
    @Ignore
    public void isFailed_successful_tx() {

        final Operation operation = Fixture.from(Operation.class).gimme("valid");

        operation.status = OperationStatus.CREATED;
        assertThat(operation.isFailed()).isFalse();

        operation.status = OperationStatus.BROADCASTED;
        assertThat(operation.isFailed()).isFalse();

        operation.status = OperationStatus.CONFIRMED;
        assertThat(operation.isFailed()).isFalse();

        operation.status = OperationStatus.SETTLED;
        assertThat(operation.isFailed()).isFalse();
    }

    @Test
    @Ignore
    public void isFailed_failed_tx() {

        final Operation operation = Fixture.from(Operation.class).gimme("valid");

        operation.status = OperationStatus.DROPPED;
        assertThat(operation.isFailed()).isTrue();

        operation.status = OperationStatus.FAILED;
        assertThat(operation.isFailed()).isTrue();
    }
}