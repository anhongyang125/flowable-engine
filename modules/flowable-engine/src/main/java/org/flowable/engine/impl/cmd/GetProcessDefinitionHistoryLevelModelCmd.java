/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.flowable.engine.impl.cmd;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.impl.history.HistoryLevel;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.repository.ProcessDefinition;

/**
 * @author Tijs Rademakers
 */
public class GetProcessDefinitionHistoryLevelModelCmd implements Command<HistoryLevel>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String processDefinitionId;

    public GetProcessDefinitionHistoryLevelModelCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public HistoryLevel execute(CommandContext commandContext) {
        if (processDefinitionId == null) {
            throw new FlowableIllegalArgumentException("processDefinitionId is null");
        }

        HistoryLevel historyLevel = null;

        ProcessDefinition processDefinition = CommandContextUtil.getProcessDefinitionEntityManager(commandContext).findById(processDefinitionId);

        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);

        Process process = bpmnModel.getProcessById(processDefinition.getKey());
        if (process.getExtensionElements().containsKey("historyLevel")) {
            ExtensionElement historyLevelElement = process.getExtensionElements().get("historyLevel").iterator().next();
            String historyLevelValue = historyLevelElement.getElementText();
            if (StringUtils.isNotEmpty(historyLevelValue)) {
                try {
                    historyLevel = HistoryLevel.getHistoryLevelForKey(historyLevelValue);

                } catch (Exception e) {
                }
            }
        }

        if (historyLevel == null) {
            historyLevel = CommandContextUtil.getProcessEngineConfiguration(commandContext).getHistoryLevel();
        }

        return historyLevel;
    }
}