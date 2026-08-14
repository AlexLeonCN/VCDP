<template>
  <el-input
    class="hex-address-input"
    :model-value="displayValue"
    :disabled="disabled"
    :placeholder="placeholder"
    spellcheck="false"
    @update:model-value="onInput"
    @blur="onBlur"
  >
    <template #prepend>0x</template>
  </el-input>
</template>

<script>
import { computed } from 'vue';
import { sanitizeHexBody } from '../utils/hex';

export default {
  name: 'HexAddressInput',
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: '请输入十六进制'
    }
  },
  emits: ['update:modelValue', 'blur', 'change'],
  setup(props, { emit }) {
    const displayValue = computed(() => sanitizeHexBody(props.modelValue));

    const onInput = value => {
      const next = sanitizeHexBody(value);
      emit('update:modelValue', next);
      emit('change', next);
    };

    const onBlur = event => {
      emit('blur', event);
    };

    return {
      displayValue,
      onInput,
      onBlur
    };
  }
};
</script>

<style scoped>
.hex-address-input :deep(.el-input-group__prepend) {
  font-family: 'JetBrains Mono', 'Cascadia Code', monospace;
  font-weight: 700;
  color: var(--tech-accent);
  padding: 0 12px;
  letter-spacing: 0.04em;
}

.hex-address-input :deep(.el-input__inner),
.hex-address-input :deep(.el-input__wrapper) {
  font-family: 'JetBrains Mono', 'Cascadia Code', monospace;
  text-transform: uppercase;
}
</style>
