package com.rawcod.jerminal.command.parameters.string;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;
import com.rawcod.jerminal.util.TrieUtils;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 18:21
 */
public class DynamicStringParam extends AbstractMandatoryCommandParam {
    private final Supplier<List<String>> valuesSupplier;

    public DynamicStringParam(String name, String description, Supplier<List<String>> valuesSupplier) {
        super(name, description);
        this.valuesSupplier = valuesSupplier;
    }

    @Override
    protected ParseParamValueReturnValue parse(String rawValue, ParamParseContext context) {
        final Trie<String> values = getValues();
        final StringParamValueParser parser = new StringParamValueParser(values, getName());
        return parser.parse(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue autoComplete(String prefix, ParamParseContext context) {
        final Trie<String> values = getValues();
        final StringParamValueAutoCompleter autoCompleter = new StringParamValueAutoCompleter(values, getName());
        return autoCompleter.autoComplete(prefix, Predicates.<String>alwaysTrue());
    }

    private Trie<String> getValues() {
        return TrieUtils.toTrie(valuesSupplier.get());
    }

    @Override
    public String toString() {
        return String.format("{%s: String}", getName());
    }
}