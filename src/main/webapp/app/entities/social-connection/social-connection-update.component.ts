import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import SocialConnectionService from './social-connection.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { type ISocialConnection, SocialConnection } from '@/shared/model/social-connection.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SocialConnectionUpdate',
  setup() {
    const socialConnectionService = inject('socialConnectionService', () => new SocialConnectionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const socialConnection: Ref<ISocialConnection> = ref(new SocialConnection());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveSocialConnection = async socialConnectionId => {
      try {
        const res = await socialConnectionService().find(socialConnectionId);
        res.createdAt = new Date(res.createdAt);
        socialConnection.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.socialConnectionId) {
      retrieveSocialConnection(route.params.socialConnectionId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      friendUsername: {
        required: validations.required('本字段不能为空.'),
      },
      status: {
        required: validations.required('本字段不能为空.'),
      },
      createdAt: {
        required: validations.required('本字段不能为空.'),
      },
      users: {},
    };
    const v$ = useVuelidate(validationRules, socialConnection as any);
    v$.value.$validate();

    return {
      socialConnectionService,
      alertService,
      socialConnection,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: socialConnection }),
    };
  },
  created(): void {
    this.socialConnection.users = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.socialConnection.id) {
        this.socialConnectionService()
          .update(this.socialConnection)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A SocialConnection is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.socialConnectionService()
          .create(this.socialConnection)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A SocialConnection is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
