package by.overpas.detekt.intellij.kotlin.styleissues

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.kotlin.styleissues.addoperatormodifier.AddOperatorModifier
import by.overpas.detekt.intellij.kotlin.styleissues.addvariancemodifier.AddVarianceModifier
import by.overpas.detekt.intellij.kotlin.styleissues.booleanliteralargument.BooleanLiteralArgument
import by.overpas.detekt.intellij.kotlin.styleissues.cascadeif.CascadeIf
import by.overpas.detekt.intellij.kotlin.styleissues.collectionconcatenationtobuildcollection.CollectionConcatenationToBuildCollection
import by.overpas.detekt.intellij.kotlin.styleissues.convertpairconstructortotofunction.ConvertPairConstructorToToFunction
import by.overpas.detekt.intellij.kotlin.styleissues.convertrangechecktotwocomparisons.ConvertRangeCheckToTwoComparisons
import by.overpas.detekt.intellij.kotlin.styleissues.convertreferencetolambda.ConvertReferenceToLambda
import by.overpas.detekt.intellij.kotlin.styleissues.convertsealedclasstosealedinterface.ConvertSealedClassToSealedInterface
import by.overpas.detekt.intellij.kotlin.styleissues.convertsealedinterfacetosealedclass.ConvertSealedInterfaceToSealedClass
import by.overpas.detekt.intellij.kotlin.styleissues.convertsecondaryconstructortoprimary.ConvertSecondaryConstructorToPrimary
import by.overpas.detekt.intellij.kotlin.styleissues.converttoexplicitbackingfields.ConvertToExplicitBackingFields
import by.overpas.detekt.intellij.kotlin.styleissues.converttostringtemplate.ConvertToStringTemplate
import by.overpas.detekt.intellij.kotlin.styleissues.converttryfinallytousecall.ConvertTryFinallyToUseCall
import by.overpas.detekt.intellij.kotlin.styleissues.converttwocomparisonstorangecheck.ConvertTwoComparisonsToRangeCheck
import by.overpas.detekt.intellij.kotlin.styleissues.copywithoutnamedarguments.CopyWithoutNamedArguments
import by.overpas.detekt.intellij.kotlin.styleissues.destructuringdeclaration.DestructuringDeclaration
import by.overpas.detekt.intellij.kotlin.styleissues.filterisinstancecallwithclassliteralargument.FilterIsInstanceCallWithClassLiteralArgument
import by.overpas.detekt.intellij.kotlin.styleissues.foldinitializerandiftoelvis.FoldInitializerAndIfToElvis
import by.overpas.detekt.intellij.kotlin.styleissues.ifthentoelvis.IfThenToElvis
import by.overpas.detekt.intellij.kotlin.styleissues.ifthentosafeaccess.IfThenToSafeAccess
import by.overpas.detekt.intellij.kotlin.styleissues.implicitthis.ImplicitThis
import by.overpas.detekt.intellij.kotlin.styleissues.introducewhensubject.IntroduceWhenSubject
import by.overpas.detekt.intellij.kotlin.styleissues.javamapforeach.JavaMapForEach
import by.overpas.detekt.intellij.kotlin.styleissues.joindeclarationandassignment.JoinDeclarationAndAssignment
import by.overpas.detekt.intellij.kotlin.styleissues.liftreturnorassignment.LiftReturnOrAssignment
import by.overpas.detekt.intellij.kotlin.styleissues.maptoforeach.MapToForEach
import by.overpas.detekt.intellij.kotlin.styleissues.movelambdaoutsideparentheses.MoveLambdaOutsideParentheses
import by.overpas.detekt.intellij.kotlin.styleissues.movevariabledeclarationintowhen.MoveVariableDeclarationIntoWhen
import by.overpas.detekt.intellij.kotlin.styleissues.nullablehashcode.NullableHashCode
import by.overpas.detekt.intellij.kotlin.styleissues.redundantassequence.RedundantAsSequence
import by.overpas.detekt.intellij.kotlin.styleissues.redundantelseinif.RedundantElseInIf
import by.overpas.detekt.intellij.kotlin.styleissues.redundantobjecttypecheck.RedundantObjectTypeCheck
import by.overpas.detekt.intellij.kotlin.styleissues.redundantruncatching.RedundantRunCatching
import by.overpas.detekt.intellij.kotlin.styleissues.removeemptyparenthesesfromannotationentry.RemoveEmptyParenthesesFromAnnotationEntry
import by.overpas.detekt.intellij.kotlin.styleissues.replaceaddallwithmapto.ReplaceAddAllWithMapTo
import by.overpas.detekt.intellij.kotlin.styleissues.replaceassertbooleanwithassertequality.ReplaceAssertBooleanWithAssertEquality
import by.overpas.detekt.intellij.kotlin.styleissues.replaceassociatefunction.ReplaceAssociateFunction
import by.overpas.detekt.intellij.kotlin.styleissues.replacecallwithbinaryoperator.ReplaceCallWithBinaryOperator
import by.overpas.detekt.intellij.kotlin.styleissues.replacecollectioncountwithsize.ReplaceCollectionCountWithSize
import by.overpas.detekt.intellij.kotlin.styleissues.replacecontains.ReplaceContains
import by.overpas.detekt.intellij.kotlin.styleissues.replaceifexpressionwithfirstornull.ReplaceIfExpressionWithFirstOrNull
import by.overpas.detekt.intellij.kotlin.styleissues.replacejavastaticmethodwithkotlinanalog.ReplaceJavaStaticMethodWithKotlinAnalog
import by.overpas.detekt.intellij.kotlin.styleissues.replacemanualrangewithindicescalls.ReplaceManualRangeWithIndicesCalls
import by.overpas.detekt.intellij.kotlin.styleissues.replacemapgetordefault.ReplaceMapGetOrDefault
import by.overpas.detekt.intellij.kotlin.styleissues.replacemapindexedwithlistgenerator.ReplaceMapIndexedWithListGenerator
import by.overpas.detekt.intellij.kotlin.styleissues.replacemapkeyscallchainwithkeys.ReplaceMapKeysCallChainWithKeys
import by.overpas.detekt.intellij.kotlin.styleissues.replacenegatedisemptywithisnotempty.ReplaceNegatedIsEmptyWithIsNotEmpty
import by.overpas.detekt.intellij.kotlin.styleissues.replacenotnullassertionwithelvisreturn.ReplaceNotNullAssertionWithElvisReturn
import by.overpas.detekt.intellij.kotlin.styleissues.replacerangestartendinclusivewithfirstlast.ReplaceRangeStartEndInclusiveWithFirstLast
import by.overpas.detekt.intellij.kotlin.styleissues.replacereadlinewithreadln.ReplaceReadLineWithReadln
import by.overpas.detekt.intellij.kotlin.styleissues.replacesizecheckwithisnotempty.ReplaceSizeCheckWithIsNotEmpty
import by.overpas.detekt.intellij.kotlin.styleissues.replacesizezerocheckwithisempty.ReplaceSizeZeroCheckWithIsEmpty
import by.overpas.detekt.intellij.kotlin.styleissues.replacestringformatwithliteral.ReplaceStringFormatWithLiteral
import by.overpas.detekt.intellij.kotlin.styleissues.replacesubstringwithdroplast.ReplaceSubstringWithDropLast
import by.overpas.detekt.intellij.kotlin.styleissues.replacesubstringwithindexingoperation.ReplaceSubstringWithIndexingOperation
import by.overpas.detekt.intellij.kotlin.styleissues.replacesubstringwithsubstringafter.ReplaceSubstringWithSubstringAfter
import by.overpas.detekt.intellij.kotlin.styleissues.replacesubstringwithsubstringbefore.ReplaceSubstringWithSubstringBefore
import by.overpas.detekt.intellij.kotlin.styleissues.replacesubstringwithtake.ReplaceSubstringWithTake
import by.overpas.detekt.intellij.kotlin.styleissues.replacetostringwithstringtemplate.ReplaceToStringWithStringTemplate
import by.overpas.detekt.intellij.kotlin.styleissues.replacetowithinfixform.ReplaceToWithInfixForm
import by.overpas.detekt.intellij.kotlin.styleissues.replaceuntilwithrangeuntil.ReplaceUntilWithRangeUntil
import by.overpas.detekt.intellij.kotlin.styleissues.replacewithcallwithcontextcall.ReplaceWithCallWithContextCall
import by.overpas.detekt.intellij.kotlin.styleissues.replacewithignorecaseequals.ReplaceWithIgnoreCaseEquals
import by.overpas.detekt.intellij.kotlin.styleissues.replacewithimportalias.ReplaceWithImportAlias
import by.overpas.detekt.intellij.kotlin.styleissues.replacewithoperatorassignment.ReplaceWithOperatorAssignment
import by.overpas.detekt.intellij.kotlin.styleissues.revertexplicitbackingfields.RevertExplicitBackingFields
import by.overpas.detekt.intellij.kotlin.styleissues.safecastwithreturn.SafeCastWithReturn
import by.overpas.detekt.intellij.kotlin.styleissues.simplifiablecall.SimplifiableCall
import by.overpas.detekt.intellij.kotlin.styleissues.simplifiablecallchain.SimplifiableCallChain
import by.overpas.detekt.intellij.kotlin.styleissues.simplifyassertnotnull.SimplifyAssertNotNull
import by.overpas.detekt.intellij.kotlin.styleissues.simplifybooleanwithconstants.SimplifyBooleanWithConstants
import by.overpas.detekt.intellij.kotlin.styleissues.simplifynegatedbinaryexpression.SimplifyNegatedBinaryExpression
import by.overpas.detekt.intellij.kotlin.styleissues.simplifynestedeachinscopefunction.SimplifyNestedEachInScopeFunction
import by.overpas.detekt.intellij.kotlin.styleissues.unclearprecedenceofbinaryexpression.UnclearPrecedenceOfBinaryExpression
import by.overpas.detekt.intellij.kotlin.styleissues.unlabeledreturninsidelambda.UnlabeledReturnInsideLambda
import by.overpas.detekt.intellij.kotlin.styleissues.unsafecastwithreturn.UnsafeCastWithReturn
import by.overpas.detekt.intellij.kotlin.styleissues.useexpressionbody.UseExpressionBody
import by.overpas.detekt.intellij.kotlin.styleissues.usepropertyaccesssyntax.UsePropertyAccessSyntax
import by.overpas.detekt.intellij.kotlin.styleissues.verbosenullabilityandemptiness.VerboseNullabilityAndEmptiness
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("group.names.style.issues")
class StyleIssuesRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-kotlin-style-issues")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::AddOperatorModifier,
                ::AddVarianceModifier,
                ::BooleanLiteralArgument,
                ::CascadeIf,
                ::CollectionConcatenationToBuildCollection,
                ::ConvertPairConstructorToToFunction,
                ::ConvertRangeCheckToTwoComparisons,
                ::ConvertReferenceToLambda,
                ::ConvertSealedClassToSealedInterface,
                ::ConvertSealedInterfaceToSealedClass,
                ::ConvertSecondaryConstructorToPrimary,
                ::ConvertToExplicitBackingFields,
                ::ConvertToStringTemplate,
                ::ConvertTryFinallyToUseCall,
                ::ConvertTwoComparisonsToRangeCheck,
                ::CopyWithoutNamedArguments,
                ::DestructuringDeclaration,
                ::FilterIsInstanceCallWithClassLiteralArgument,
                ::FoldInitializerAndIfToElvis,
                ::IfThenToElvis,
                ::IfThenToSafeAccess,
                ::ImplicitThis,
                ::IntroduceWhenSubject,
                ::JavaMapForEach,
                ::JoinDeclarationAndAssignment,
                ::LiftReturnOrAssignment,
                ::MapToForEach,
                ::MoveLambdaOutsideParentheses,
                ::MoveVariableDeclarationIntoWhen,
                ::NullableHashCode,
                ::RedundantAsSequence,
                ::RedundantElseInIf,
                ::RedundantObjectTypeCheck,
                ::RedundantRunCatching,
                ::RemoveEmptyParenthesesFromAnnotationEntry,
                ::ReplaceAddAllWithMapTo,
                ::ReplaceAssertBooleanWithAssertEquality,
                ::ReplaceAssociateFunction,
                ::ReplaceCallWithBinaryOperator,
                ::ReplaceCollectionCountWithSize,
                ::ReplaceContains,
                ::ReplaceIfExpressionWithFirstOrNull,
                ::ReplaceJavaStaticMethodWithKotlinAnalog,
                ::ReplaceManualRangeWithIndicesCalls,
                ::ReplaceMapGetOrDefault,
                ::ReplaceMapIndexedWithListGenerator,
                ::ReplaceMapKeysCallChainWithKeys,
                ::ReplaceNegatedIsEmptyWithIsNotEmpty,
                ::ReplaceNotNullAssertionWithElvisReturn,
                ::ReplaceRangeStartEndInclusiveWithFirstLast,
                ::ReplaceReadLineWithReadln,
                ::ReplaceSizeCheckWithIsNotEmpty,
                ::ReplaceSizeZeroCheckWithIsEmpty,
                ::ReplaceStringFormatWithLiteral,
                ::ReplaceSubstringWithDropLast,
                ::ReplaceSubstringWithIndexingOperation,
                ::ReplaceSubstringWithSubstringAfter,
                ::ReplaceSubstringWithSubstringBefore,
                ::ReplaceSubstringWithTake,
                ::ReplaceToStringWithStringTemplate,
                ::ReplaceToWithInfixForm,
                ::ReplaceUntilWithRangeUntil,
                ::ReplaceWithCallWithContextCall,
                ::ReplaceWithIgnoreCaseEquals,
                ::ReplaceWithImportAlias,
                ::ReplaceWithOperatorAssignment,
                ::RevertExplicitBackingFields,
                ::SafeCastWithReturn,
                ::SimplifiableCall,
                ::SimplifiableCallChain,
                ::SimplifyAssertNotNull,
                ::SimplifyBooleanWithConstants,
                ::SimplifyNegatedBinaryExpression,
                ::SimplifyNestedEachInScopeFunction,
                ::UnclearPrecedenceOfBinaryExpression,
                ::UnlabeledReturnInsideLambda,
                ::UnsafeCastWithReturn,
                ::UseExpressionBody,
                ::UsePropertyAccessSyntax,
                ::VerboseNullabilityAndEmptiness,
            ),
        )
}
