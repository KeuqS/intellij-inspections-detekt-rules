package by.overpas.detekt.intellij.kotlin.redundantconstructs

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.kotlin.redundantconstructs.canbeparameter.CanBeParameter
import by.overpas.detekt.intellij.kotlin.redundantconstructs.canbeprimaryconstructorproperty.CanBePrimaryConstructorProperty
import by.overpas.detekt.intellij.kotlin.redundantconstructs.canunescapedollarliteral.CanUnescapeDollarLiteral
import by.overpas.detekt.intellij.kotlin.redundantconstructs.constantconditionif.ConstantConditionIf
import by.overpas.detekt.intellij.kotlin.redundantconstructs.explicitthis.ExplicitThis
import by.overpas.detekt.intellij.kotlin.redundantconstructs.ifexpressionwithidenticalbranches.IfExpressionWithIdenticalBranches
import by.overpas.detekt.intellij.kotlin.redundantconstructs.kotlinredundantoverride.KotlinRedundantOverride
import by.overpas.detekt.intellij.kotlin.redundantconstructs.nullcheckstosafecall.NullChecksToSafeCall
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantcompanionreference.RedundantCompanionReference
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantelvisreturnnull.RedundantElvisReturnNull
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantenumconstructorinvocation.RedundantEnumConstructorInvocation
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantgetter.RedundantGetter
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantif.RedundantIf
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantinterpolationprefix.RedundantInterpolationPrefix
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantlabeledreturnonlastexpressioninlambda.RedundantLabeledReturnOnLastExpressionInLambda
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantlambdaarrow.RedundantLambdaArrow
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantlambdaoranonymousfunction.RedundantLambdaOrAnonymousFunction
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantmodalitymodifier.RedundantModalityModifier
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantnullablereturntype.RedundantNullableReturnType
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantreturnkeyword.RedundantReturnKeyword
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantreturnlabel.RedundantReturnLabel
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantsamconstructor.RedundantSamConstructor
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantsetter.RedundantSetter
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantupperbound.RedundantUpperBound
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantvalueargument.RedundantValueArgument
import by.overpas.detekt.intellij.kotlin.redundantconstructs.redundantwith.RedundantWith
import by.overpas.detekt.intellij.kotlin.redundantconstructs.removeexplicitsuperqualifier.RemoveExplicitSuperQualifier
import by.overpas.detekt.intellij.kotlin.redundantconstructs.removeexplicittypearguments.RemoveExplicitTypeArguments
import by.overpas.detekt.intellij.kotlin.redundantconstructs.removeredundantqualifiername.RemoveRedundantQualifierName
import by.overpas.detekt.intellij.kotlin.redundantconstructs.removeredundantspreadoperator.RemoveRedundantSpreadOperator
import by.overpas.detekt.intellij.kotlin.redundantconstructs.removesetterparametertype.RemoveSetterParameterType
import by.overpas.detekt.intellij.kotlin.redundantconstructs.removesingleexpressionstringtemplate.RemoveSingleExpressionStringTemplate
import by.overpas.detekt.intellij.kotlin.redundantconstructs.simplifywhenwithbooleanconstantcondition.SimplifyWhenWithBooleanConstantCondition
import by.overpas.detekt.intellij.kotlin.redundantconstructs.suspiciouscallablereferenceinlambda.SuspiciousCallableReferenceInLambda
import by.overpas.detekt.intellij.kotlin.redundantconstructs.unnecessaryoptinannotation.UnnecessaryOptInAnnotation
import by.overpas.detekt.intellij.kotlin.redundantconstructs.unnecessaryvariable.UnnecessaryVariable
import by.overpas.detekt.intellij.kotlin.redundantconstructs.unusedcontextparametercall.UnusedContextParameterCall
import by.overpas.detekt.intellij.kotlin.redundantconstructs.unusedreceiverparameter.UnusedReceiverParameter
import by.overpas.detekt.intellij.kotlin.redundantconstructs.variableinitializerisredundant.VariableInitializerIsRedundant
import by.overpas.detekt.intellij.kotlin.redundantconstructs.whenwithonlyelse.WhenWithOnlyElse
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("group.names.redundant.constructs")
class RedundantConstructsRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-kotlin-redundant-constructs")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::CanBeParameter,
                ::CanBePrimaryConstructorProperty,
                ::CanUnescapeDollarLiteral,
                ::ConstantConditionIf,
                ::ExplicitThis,
                ::IfExpressionWithIdenticalBranches,
                ::KotlinRedundantOverride,
                ::NullChecksToSafeCall,
                ::RedundantCompanionReference,
                ::RedundantElvisReturnNull,
                ::RedundantEnumConstructorInvocation,
                ::RedundantGetter,
                ::RedundantIf,
                ::RedundantInterpolationPrefix,
                ::RedundantLabeledReturnOnLastExpressionInLambda,
                ::RedundantLambdaArrow,
                ::RedundantLambdaOrAnonymousFunction,
                ::RedundantModalityModifier,
                ::RedundantNullableReturnType,
                ::RedundantReturnKeyword,
                ::RedundantReturnLabel,
                ::RedundantSamConstructor,
                ::RedundantSetter,
                ::RedundantUpperBound,
                ::RedundantValueArgument,
                ::RedundantWith,
                ::RemoveExplicitSuperQualifier,
                ::RemoveExplicitTypeArguments,
                ::RemoveRedundantQualifierName,
                ::RemoveRedundantSpreadOperator,
                ::RemoveSetterParameterType,
                ::RemoveSingleExpressionStringTemplate,
                ::SimplifyWhenWithBooleanConstantCondition,
                ::SuspiciousCallableReferenceInLambda,
                ::UnnecessaryOptInAnnotation,
                ::UnnecessaryVariable,
                ::UnusedContextParameterCall,
                ::UnusedReceiverParameter,
                ::VariableInitializerIsRedundant,
                ::WhenWithOnlyElse,
            ),
        )
}
