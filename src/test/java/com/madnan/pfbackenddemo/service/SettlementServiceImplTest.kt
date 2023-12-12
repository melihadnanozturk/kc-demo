package com.madnan.pfbackenddemo.service

import com.madnan.pfbackenddemo.model.TaskRepresentation
import com.madnan.pfbackenddemo.service.pf.SettlementServiceImpl
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SettlementServiceImplTest {

    @InjectMocks
    lateinit var refundService: SettlementServiceImpl

    @Mock
    lateinit var processService: ProcessService

    @Test
    fun testCreateSettlementOperation() {
        val testTaskRep = TaskRepresentation()
        Mockito.`when`(processService.startProcess(Mockito.anyMap(), Mockito.anyString(), Mockito.any()))
                .thenReturn(testTaskRep);

        val testResponseData = refundService.createSettlementOperation()

        assertNotNull(testResponseData)
        Mockito.verify(processService, times(1))
                .startProcess(Mockito.anyMap(), Mockito.anyString(), Mockito.any())
    }

}