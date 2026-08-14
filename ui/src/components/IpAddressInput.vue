<template>
  <div class="ip-address-input" :class="{ disabled: disabled }">
    <template v-for="(segment, index) in segments" :key="index">
      <input
        :ref="el => setInputRef(el, index)"
        class="ip-segment"
        maxlength="3"
        :disabled="disabled"
        :value="segment"
        @input="onInput(index, $event)"
        @keydown="onKeydown(index, $event)"
        @paste="onPaste($event)"
      />
      <span v-if="index < 3" class="ip-separator">.</span>
    </template>
  </div>
</template>

<script>
import { ref, watch } from 'vue';

export default {
  name: 'IpAddressInput',
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
    const segments = ref(['', '', '', '']);
    const inputRefs = ref([]);

    const setInputRef = (el, index) => {
      if (el) {
        inputRefs.value[index] = el;
      }
    };

    const clampSegment = value => {
      const digits = String(value || '').replace(/\D/g, '').slice(0, 3);
      if (!digits) {
        return '';
      }
      const num = Math.min(255, Number(digits));
      return String(num);
    };

    const normalizeToSegments = value => {
      const parts = String(value || '').split('.');
      const next = ['', '', '', ''];
      for (let i = 0; i < 4; i += 1) {
        next[i] = clampSegment(parts[i] || '');
      }
      return next;
    };

    const emitValue = () => {
      emit('update:modelValue', segments.value.join('.'));
    };

    watch(
      () => props.modelValue,
      value => {
        const joined = segments.value.join('.');
        if (joined !== String(value || '')) {
          segments.value = normalizeToSegments(value);
        }
      },
      { immediate: true }
    );

    const onInput = (index, event) => {
      const raw = String(event.target.value || '').replace(/\D/g, '').slice(0, 3);
      const next = [...segments.value];
      next[index] = clampSegment(raw);
      segments.value = next;
      event.target.value = next[index];
      emitValue();
      if (raw.length >= 3 && index < 3) {
        inputRefs.value[index + 1]?.focus();
      }
    };

    const onKeydown = (index, event) => {
      if (event.key === '.' || event.key === ' ') {
        event.preventDefault();
        if (index < 3) {
          inputRefs.value[index + 1]?.focus();
        }
        return;
      }
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
.ip-address-input {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  width: 100%;
}

.ip-segment {
  width: 52px;
  height: 32px;
  text-align: center;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  background: var(--el-fill-color-blank);
  color: var(--el-text-color-primary);
  font-family: 'JetBrains Mono', 'Cascadia Code', monospace;
}

.ip-segment:focus {
  outline: none;
  border-color: var(--tech-accent);
  box-shadow: 0 0 0 1px rgba(0, 212, 255, 0.25);
}

.ip-separator {
  color: var(--tech-muted);
  font-weight: 600;
}

.disabled .ip-segment {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-placeholder);
}
</style>
