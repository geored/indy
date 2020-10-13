package org.commonjava.indy.promote.rules

import org.apache.commons.lang.StringUtils
import org.commonjava.indy.promote.validate.PromotionValidationException
import org.commonjava.indy.promote.validate.model.ValidationRequest
import org.commonjava.indy.promote.validate.model.ValidationRule

class NPMNoPreExistingPaths implements ValidationRule {

    String validate(ValidationRequest request) throws PromotionValidationException {
        def verifyStoreKeys = request.getTools().getValidationStoreKeys(request, false);

        def errors = Collections.synchronizedList(new ArrayList());
        def tools = request.getTools()

        tools.paralleledEach(request.getSourcePaths(), { it ->
            tools.forEach(verifyStoreKeys, { verifyStoreKey ->
                if (tools.exists(verifyStoreKey, it)) {
                    errors.add(String.format("%s is already available in: %s", it, verifyStoreKey))
                }
            })
        })

        errors.isEmpty() ? null: StringUtils.join(errors, "\n")
    }
}
