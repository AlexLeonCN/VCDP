<template>
  <div class="mac-address-input" :class="{ disabled: disabled }">
    <template v-for="(segment, index) in segments" :key="index">
      <input
        :ref="el => setInputRef(el, index)"
        class="mac-segment"
        maxlength="2"
        :disabled="disabled"
        :value="segment"
        @input="onInput(index, $event)"
        @keydown="onKeydown(index, $event)"
        @paste="onPaste($event)"
      />
      <span v-if="index < 5" class="mac-separator">:</span>
    </template>
  </div>
</template>

<script>
import { ref, watch } from 'vue';

export default {
  name: 'MacAddressInput',
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const segments = ref(['', '', '', '', '', '']);
    const inputRefs = ref([]);

    const setInputRef = (el, index) => {
      if (el) {
        inputRefs.value[index] = el;
      }
    };

    const normalizeToSegments = value => {
      const raw = String(value || '')
        .replace(/[^0-9a-fA-F]/g, '')
        .toUpperCase()
        .slice(0, 12);
      const next = ['', '', '', '', '', ''];
      for (let i = 0; i < 6; i += 1) {
        next[i] = raw.slice(i * 2, i * 2 + 2);
      }
      return next;
    };

    const emitValue = () => {
      emit('update:modelValue', segments.value.join(''));
    };

    watch(
      () => props.modelValue,
      value => {
        const joined = segments.value.join('');
        const normalized = String(value || '')
          .replace(/[^0-9a-fA-F]/g, '')
          .toUpperCase();
        if (joined !== normalized) {
          segments.value = normalizeToSegments(value);
        }
      },
      { immediate: true }
    );

    const onInput = (index, event) => {
      const cleaned = String(event.target.value || '')
        .replace(/[^0-9a-fA-F]/g, '')
        .toUpperCase()
        .slice(0, 2);
      const next = [...segments.value];
      next[index] = cleaned;
      segments.value = next;
      event.target.value = cleaned;
      emitValue();
      if (cleaned.length === 2 && index < 5) {
        inputRefs.value[index + 1]?.focus();
      }
    };

    const onKeydown = (index, event) => {
      if (event.key === 'Backspace' && !segments.value[index] && index > 0) {
        inputRefs.value[index - 1]?.focus();
      }
    };

    const onPaste = event => {
      event.preventDefault();
      const text = event.clipboardData?.getData('text') || '';
      segments.value = normalizeToSegments(text);
      emitValue();
    };

    return {
      segments,
      setInputRef,
      onInput,
      onKeydown,
      onPaste
    };
  }
};
</script>

<style scoped>
.mac-address-input {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  width: 100%;
}

.mac-segment {
  width: 42px;
  height: 32px;
  text-align: center;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  background: var(--el-fill-color-blank);
  color: var(--el-text-color-primary);
  font-family: 'JetBrains Mono', 'Cascadia Code', monospace;
  text-transform: uppercase;
}

.mac-segment:focus {
  outline: none;
  border-color: var(--tech-accent);
  box-shadow: 0 0 0 1px rgba(0, 212, 255, 0.25);
}

.mac-separator {
  color: var(--tech-muted);
  font-weight: 600;
}

.disabled .mac-segment {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-placeholder);
}
</style>
