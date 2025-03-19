import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import SocialConnection from './social-connection.vue';
import SocialConnectionService from './social-connection.service';
import AlertService from '@/shared/alert/alert.service';

type SocialConnectionComponentType = InstanceType<typeof SocialConnection>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('SocialConnection Management Component', () => {
    let socialConnectionServiceStub: SinonStubbedInstance<SocialConnectionService>;
    let mountOptions: MountingOptions<SocialConnectionComponentType>['global'];

    beforeEach(() => {
      socialConnectionServiceStub = sinon.createStubInstance<SocialConnectionService>(SocialConnectionService);
      socialConnectionServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          socialConnectionService: () => socialConnectionServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        socialConnectionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(SocialConnection, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(socialConnectionServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.socialConnections[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(SocialConnection, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(socialConnectionServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: SocialConnectionComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(SocialConnection, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        socialConnectionServiceStub.retrieve.reset();
        socialConnectionServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        socialConnectionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(socialConnectionServiceStub.retrieve.called).toBeTruthy();
        expect(comp.socialConnections[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(socialConnectionServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        socialConnectionServiceStub.retrieve.reset();
        socialConnectionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(socialConnectionServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.socialConnections[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(socialConnectionServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        socialConnectionServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeSocialConnection();
        await comp.$nextTick(); // clear components

        // THEN
        expect(socialConnectionServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(socialConnectionServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
