/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.ala;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.PipelineEventListener;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.local.InMemoryPipelineRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Base class for testing the build pipelines.
 */
public class BuildPipelineExecutionTestBase
        extends BuildPipelineTestBase {

    protected PipelineRegistry pipelineRegistry = new InMemoryPipelineRegistry( );

    protected Pipeline pipe;

    protected Input input;

    protected BuildPipelineInitializer pipelineInitializer;

    @Mock
    protected PipelineEventListener pipelineEventListener;

    protected Collection< ConfigExecutor > configs = new ArrayList<>( );

    protected void verifyStages( String... stageNames ) {
        // verify that the pipeline stages has been properly initialized.
        assertNotNull( pipe );
        List< Stage > stages = pipe.getStages( );
        assertEquals( stageNames.length, stages.size( ) );
        for ( int i = 0; i < stageNames.length; i++ ) {
            assertEquals( stageNames[ i ], stages.get( i ).getName( ) );
        }
    }

    protected void verifyPipelineEvents( Pipeline pipe ) {
        ArgumentCaptor< BeforePipelineExecutionEvent > beforePipelineExecutionCaptor = ArgumentCaptor.forClass( BeforePipelineExecutionEvent.class );
        ArgumentCaptor< BeforeStageExecutionEvent > beforeStageExecutionCaptor = ArgumentCaptor.forClass( BeforeStageExecutionEvent.class );
        ArgumentCaptor< AfterStageExecutionEvent > afterStageExecutionCaptor = ArgumentCaptor.forClass( AfterStageExecutionEvent.class );
        ArgumentCaptor< AfterPipelineExecutionEvent > afterPipelineExecutionCaptor = ArgumentCaptor.forClass( AfterPipelineExecutionEvent.class );

        // verify the pipeline initialization event was raised.
        verify( pipelineEventListener, times( 1 ) ).beforePipelineExecution( beforePipelineExecutionCaptor.capture( ) );
        assertEquals( pipe, beforePipelineExecutionCaptor.getValue( ).getPipeline( ) );

        // verify the initialization and finalization events were properly raised for current pipe stages.
        verify( pipelineEventListener, times( 4 ) ).beforeStageExecution( beforeStageExecutionCaptor.capture( ) );
        verify( pipelineEventListener, times( 4 ) ).afterStageExecution( afterStageExecutionCaptor.capture( ) );

        for ( int i = 0; i < pipe.getStages( ).size( ); i++ ) {
            assertEquals( pipe.getStages( ).get( i ), beforeStageExecutionCaptor.getAllValues( ).get( i ).getStage( ) );
            assertEquals( pipe, beforeStageExecutionCaptor.getAllValues( ).get( i ).getPipeline( ) );
            assertEquals( pipe.getStages( ).get( i ), afterStageExecutionCaptor.getAllValues( ).get( i ).getStage( ) );
            assertEquals( pipe, afterStageExecutionCaptor.getAllValues( ).get( i ).getPipeline( ) );
        }

        // verify the pipeline finalization event was raised.
        verify( pipelineEventListener, times( 1 ) ).afterPipelineExecution( afterPipelineExecutionCaptor.capture( ) );
        assertEquals( pipe, afterPipelineExecutionCaptor.getValue( ).getPipeline( ) );
    }
}